package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SapSalesRepository
import com.example.model.ActiveDataSource
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DataUploadScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rawInputText by remember { mutableStateOf(SapSalesRepository.getDemoTemplateJson()) }
    var replaceExisting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var isProcessingFile by remember { mutableStateOf(false) }

    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val activeDataSource by SapSalesRepository.activeDataSource.collectAsState()
    val isSyncing by SapSalesRepository.isSyncing.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Android System File Picker for Excel (.xlsx, .xls), CSV, TSV, and JSON
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessingFile = true
            scope.launch {
                try {
                    var displayName = "uploaded_sales_data"
                    var fileSizeKb = 0L

                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (cursor.moveToFirst()) {
                            if (nameIndex != -1) displayName = cursor.getString(nameIndex)
                            if (sizeIndex != -1) fileSizeKb = (cursor.getLong(sizeIndex) / 1024L).coerceAtLeast(1L)
                        }
                    }

                    selectedFileName = displayName
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val result = SapSalesRepository.parseUploadedInputStream(inputStream, displayName, fileSizeKb)
                        if (result.isSuccess) {
                            val count = result.getOrDefault(0)
                            statusMessage = "Success! Ingested $count records from '$displayName'. All dashboard KPIs and ledgers updated in real time!"
                            isError = false
                            Toast.makeText(context, "Loaded $count records from $displayName", Toast.LENGTH_LONG).show()
                        } else {
                            statusMessage = "Error parsing file: ${result.exceptionOrNull()?.localizedMessage ?: "Unknown format"}"
                            isError = true
                        }
                    } else {
                        statusMessage = "Could not open stream for selected file."
                        isError = true
                    }
                } catch (e: Exception) {
                    statusMessage = "Failed to read file: ${e.localizedMessage}"
                    isError = true
                } finally {
                    isProcessingFile = false
                }
            }
        }
    }

    fun processManualImport() {
        val result = SapSalesRepository.importJsonData(rawInputText, replaceExisting)
        if (result.isSuccess) {
            val count = result.getOrDefault(0)
            statusMessage = "Success! Ingested $count records into the live system. Total active ledger documents: ${billingItems.size}."
            isError = false
        } else {
            statusMessage = "Import Failed: ${result.exceptionOrNull()?.localizedMessage ?: "Invalid JSON/Excel format"}"
            isError = true
        }
    }

    Scaffold(
        topBar = {
            Surface(color = GalanaNavy, shadowElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Excel & Data Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Device File Upload • Live SAP DB • Revert",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onNavigate(ScreenRoute.SAP_LIVE_SYNC) }
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "View Ledger", tint = Color.White)
                    }
                }
            }
        },
        containerColor = BackgroundLight,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. ACTIVE DATA SOURCE & REVERT BANNER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (activeDataSource) {
                            is ActiveDataSource.FileUploaded -> Color(0xFF0F2E1E) // Forest dark green
                            is ActiveDataSource.LiveSapApi -> Color(0xFF0D253F) // Deep SAP blue
                            is ActiveDataSource.StaticDefault -> Color(0xFF0F1E36) // Brand navy
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (activeDataSource) {
                                                is ActiveDataSource.FileUploaded -> FuelPetrolGreen
                                                is ActiveDataSource.LiveSapApi -> Color(0xFF38BDF8)
                                                is ActiveDataSource.StaticDefault -> GalanaAmberLight
                                            }
                                        )
                                )
                                Text(
                                    text = when (activeDataSource) {
                                        is ActiveDataSource.FileUploaded -> "CUSTOM UPLOADED FILE ACTIVE"
                                        is ActiveDataSource.LiveSapApi -> "LIVE SAP CLOUD DB ACTIVE"
                                        is ActiveDataSource.StaticDefault -> "BASELINE STATIC DATA ACTIVE"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (activeDataSource) {
                                        is ActiveDataSource.FileUploaded -> Color(0xFF86EFAC)
                                        is ActiveDataSource.LiveSapApi -> Color(0xFFBAE6FD)
                                        is ActiveDataSource.StaticDefault -> GalanaAmberLight
                                    },
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Text(
                                text = "${billingItems.size} Records",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        when (val src = activeDataSource) {
                            is ActiveDataSource.FileUploaded -> {
                                Text(
                                    text = "File: ${src.fileName} (${src.recordCount} items • ${src.fileSizeKb} KB)\nUploaded: ${src.uploadTime}. All dashboard KPIs and reports are currently calculated from this file in real time.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 17.sp
                                )

                                Button(
                                    onClick = {
                                        val count = SapSalesRepository.removeUploadedFileAndRevert()
                                        selectedFileName = null
                                        statusMessage = "Uploaded file removed. Reverted to static baseline dataset ($count records)."
                                        isError = false
                                        Toast.makeText(context, "Reverted to static baseline data", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("remove_uploaded_file_button")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Remove Uploaded File & Revert to Static Data", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            is ActiveDataSource.LiveSapApi -> {
                                Text(
                                    text = "Connected to SAP S/4HANA OData service.\nSynced: ${src.syncTime} (${src.recordCount} items). All dashboard numbers reflect the live ERP database.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 17.sp
                                )

                                OutlinedButton(
                                    onClick = {
                                        SapSalesRepository.removeUploadedFileAndRevert()
                                        statusMessage = "Reverted to baseline static data."
                                        isError = false
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Revert to Static Baseline", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                            is ActiveDataSource.StaticDefault -> {
                                Text(
                                    text = "The application is currently running on the official SAP S/4HANA static baseline data (6 billing documents, KES 11.64M total net revenue). You can upload your own Excel file or fetch live data from the SAP DB below.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. DEVICE FILE UPLOADER (Excel .xlsx / .csv / .json)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = FuelPetrolGreen, modifier = Modifier.size(24.dp))
                            Column {
                                Text(
                                    text = "Upload File from Device",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GalanaNavy
                                )
                                Text(
                                    text = "Supports Excel (.xlsx), CSV, TSV, and JSON formats",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Upload dropzone button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .clickable {
                                    filePickerLauncher.launch("*/*")
                                }
                                .padding(vertical = 20.dp, horizontal = 16.dp)
                                .testTag("select_file_from_device_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isProcessingFile) {
                                    CircularProgressIndicator(color = GalanaNavy, modifier = Modifier.size(28.dp))
                                    Text("Parsing file data...", fontSize = 12.sp, color = GalanaNavy, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = GalanaNavy,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = selectedFileName ?: "Tap to Browse & Upload Excel / CSV / JSON",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = GalanaNavy
                                    )
                                    Text(
                                        text = "Opens Android file selector to choose file from storage or Drive",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Collapsible Excel format guide
                        var showFormatGuide by remember { mutableStateOf(false) }
                        val clipboard = LocalClipboardManager.current
                        val sampleCsvTemplate = "BillingDocument,BillingDocumentItem,BillingDocumentDate,SalesOrganization,LocationID,PlantName,CustomerID,CustomerName,CustomerType,ProductID,ProductName,ProductCategory,SalesUnit,TransactionCurrency,Quantity,NetAmount,GrossAmount,DiscountAmount\n90001042,000010,2026-09-26,1000,1000,Mombasa Depot,KG-01,KenGen Power,Industrial,DIESEL,Diesel (AGO),Fuel,KL,KES,32500,4620000,5370000,750000\n90001043,000010,2026-09-26,1000,1000,Mombasa Depot,KPA-01,Kenya Ports Authority,Industrial,SUPER-PMS,Super Petrol (PMS),Fuel,KL,KES,24200,2840000,3240000,400000\n90001044,000010,2026-09-25,1000,2000,Nairobi West,KQ-01,Kenya Airways (KQ),Fleet,ATF-JET,Aviation Turbine Fuel (Jet A-1),Fuel,KL,KES,3840,720000,860000,140000\n90001045,000010,2026-09-24,1000,1000,Mombasa Depot,BC-01,Bamburi Cement Ltd,Industrial,LUBE-15W40,Engine Oil 15W40,Lubricants,KL,KES,600,480000,630000,150000"

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showFormatGuide = !showFormatGuide },
                            color = Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GalanaNavy, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (showFormatGuide) "Hide Excel Column Format Guide" else "View Accepted Excel Columns & Format Guide",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GalanaNavy
                                    )
                                }
                                Icon(
                                    imageVector = if (showFormatGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = GalanaNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showFormatGuide) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Accepted Column Names (Row 1):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = GalanaNavy
                                )
                                Text(
                                    text = "• Full SAP S/4HANA (18 cols): BillingDocument, CustomerName, PlantName, ProductName, Quantity, NetAmount, GrossAmount, DiscountAmount, ProductCategory, CustomerType, BillingDocumentDate, SalesOrganization, LocationID, CustomerID, ProductID, SalesUnit, TransactionCurrency\n\n• Minimal Quick Mode (6 cols): BillingDocument, CustomerName, PlantName, ProductName, Quantity, NetAmount",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 15.sp
                                )

                                Button(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(sampleCsvTemplate))
                                        Toast.makeText(context, "Copied Excel CSV template to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Excel CSV Template to Clipboard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. GET LIVE DATA DIRECTLY FROM SAP DB
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(22.dp))
                            Column {
                                Text(
                                    text = "SAP S/4HANA Cloud Direct API",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GalanaNavy
                                )
                                Text(
                                    text = "Client 080 • ZANI_UI_GAL_SALES OData Service",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Text(
                            text = "Fetch live BillingItem ledger records directly from the SAP cloud backend into the application in real time:",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    val result = SapSalesRepository.syncWithSapCloud()
                                    if (result.isSuccess) {
                                        val count = result.getOrDefault(0)
                                        statusMessage = "Success! Fetched $count live billing records directly from SAP S/4HANA DB. App updated in real time."
                                        isError = false
                                        Toast.makeText(context, "Synced $count records from SAP DB", Toast.LENGTH_SHORT).show()
                                    } else {
                                        statusMessage = "Could not sync: ${result.exceptionOrNull()?.localizedMessage}"
                                        isError = true
                                    }
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("get_live_sap_data_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Connecting to SAP Cloud...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Get Live Data from SAP DB", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // 4. FAST 1-TAP DEMO PRESETS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = GalanaAmberDark, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Instant Demo Presets",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                        }

                        Text(
                            text = "Inject realistic scenarios during executive presentations with a single tap:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val count = SapSalesRepository.loadPresetDemo("vessel_offtake")
                                    statusMessage = "Loaded Maritime Vessel Offtake preset! Total active documents: $count."
                                    isError = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GalanaAmber),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text("Berth 1 Vessel (+2)", fontSize = 11.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val count = SapSalesRepository.loadPresetDemo("rift_agriculture")
                                    statusMessage = "Loaded Inland Rift Valley Agri Harvest preset! Total active documents: $count."
                                    isError = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FuelDieselBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text("Rift Valley (+2)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val count = SapSalesRepository.removeUploadedFileAndRevert()
                                    selectedFileName = null
                                    statusMessage = "Reset to baseline static records ($count documents)."
                                    isError = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text("Reset Base", fontSize = 11.sp, color = GalanaNavy, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // 5. PASTE JSON / CSV DATA INPUT
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Manual Paste (JSON / CSV)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(
                                    onClick = { rawInputText = SapSalesRepository.getDemoTemplateJson() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Template", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                TextButton(
                                    onClick = { rawInputText = SapSalesRepository.exportCurrentDataAsJson() },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Copy Current", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = rawInputText,
                            onValueChange = { rawInputText = it },
                            placeholder = { Text("Paste JSON array or CSV payload here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("data_upload_text_input"),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Checkbox(
                                    checked = replaceExisting,
                                    onCheckedChange = { replaceExisting = it },
                                    colors = CheckboxDefaults.colors(checkedColor = GalanaNavy)
                                )
                                Text(
                                    text = "Replace existing dataset",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            TextButton(onClick = { rawInputText = "" }) {
                                Text("Clear", fontSize = 12.sp, color = StatusNegative)
                            }
                        }

                        Button(
                            onClick = { processManualImport() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("ingest_data_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ingest & Update Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Status / Error Banner
            statusMessage?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isError) StatusNegative else FuelPetrolGreen
                            )
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isError) Color(0xFF991B1B) else Color(0xFF166534)
                            )
                        }
                    }
                }
            }

            // Active Documents Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Ledger Documents (${billingItems.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                            Button(
                                onClick = { onNavigate(ScreenRoute.SAP_LIVE_SYNC) },
                                colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("View Ledger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = Color(0xFFF1F5F9))

                        billingItems.take(6).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.customerName} (#${item.billingDocument})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${item.productName} • ${currencyFormat.format(item.quantity)} ${item.salesUnit} @ ${item.plantName}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "KES ${currencyFormat.format(item.netAmount)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GalanaNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
