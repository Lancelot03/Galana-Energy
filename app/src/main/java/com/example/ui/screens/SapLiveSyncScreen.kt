package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SapSalesRepository
import com.example.model.ActiveDataSource
import com.example.model.SapBillingItem
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SapLiveSyncScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionInfo by SapSalesRepository.connectionInfo.collectAsState()
    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val activeDataSource by SapSalesRepository.activeDataSource.collectAsState()
    val isSyncing by SapSalesRepository.isSyncing.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedDocument by remember { mutableStateOf<SapBillingItem?>(null) }
    var syncFeedbackMessage by remember { mutableStateOf<String?>(null) }

    val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

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
                                text = "SAP S/4HANA Cloud Live",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Service: ${connectionInfo.serviceDefinition}",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    // Sync Button
                    IconButton(
                        onClick = {
                            scope.launch {
                                val res = SapSalesRepository.syncWithSapCloud()
                                syncFeedbackMessage = "Synced ${res.getOrDefault(billingItems.size)} OData billing items from SAP Cloud."
                            }
                        },
                        enabled = !isSyncing,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .size(36.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // SAP Connection Status & Endpoint Metadata Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36))
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(FuelPetrolGreen)
                                )
                                Text(
                                    text = "SAP ODATA 4.0 BACKEND",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GalanaAmberLight,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Client ${connectionInfo.client}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${connectionInfo.baseUrl}${connectionInfo.servicePath}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF94A3B8),
                            lineHeight = 15.sp
                        )

                        Divider(color = Color.White.copy(alpha = 0.1f))

                        // Entity Sets Pills
                        Column {
                            Text(
                                text = "Detected SAP Entity Sets:",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(connectionInfo.detectedEntitySets) { entity ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(entity, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Last Synced: ${connectionInfo.lastSyncTime.take(19).replace("T", " ")} UTC",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Binding: ${connectionInfo.serviceBinding}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FuelPetrolGreen
                            )
                        }
                    }
                }
            }

            // Financial Summary Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SapMetricCard(
                        label = "Gross Billed",
                        value = "KES ${currencyFormat.format(billingItems.sumOf { it.grossAmount })}",
                        sublabel = "Across ${billingItems.size} Documents",
                        color = GalanaNavy,
                        modifier = Modifier.weight(1f)
                    )
                    SapMetricCard(
                        label = "Net Revenue",
                        value = "KES ${currencyFormat.format(billingItems.sumOf { it.netAmount })}",
                        sublabel = "Excl. KES ${currencyFormat.format(billingItems.sumOf { it.discountAmount })} Disc",
                        color = FuelPetrolGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sync and Revert Quick Actions Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Live SAP DB Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                            Text(
                                text = when (activeDataSource) {
                                    is ActiveDataSource.FileUploaded -> "Custom File Active"
                                    is ActiveDataSource.LiveSapApi -> "Live SAP DB Active"
                                    ActiveDataSource.StaticDefault -> "Static Baseline Active"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val res = SapSalesRepository.syncWithSapCloud()
                                        syncFeedbackMessage = "Fetched ${res.getOrDefault(billingItems.size)} records directly from live SAP DB. App updated in real time."
                                    }
                                },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Syncing...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fetch Live SAP DB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (activeDataSource != ActiveDataSource.StaticDefault) {
                                OutlinedButton(
                                    onClick = {
                                        SapSalesRepository.removeUploadedFileAndRevert()
                                        syncFeedbackMessage = "Reverted to static baseline dataset (${billingItems.size} records)."
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Revert Base", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Real SAP Document Ledger Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real SAP S/4HANA Billing Documents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GalanaNavy
                    )
                    Text(
                        text = "${billingItems.size} Entries",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }

            // Real Billing Items from SAP
            items(billingItems) { item ->
                SapBillingItemCard(
                    item = item,
                    currencyFormat = currencyFormat,
                    onClick = { selectedDocument = item }
                )
            }
        }
    }

    // Modal Document Inspection Dialog
    selectedDocument?.let { doc ->
        AlertDialog(
            onDismissRequest = { selectedDocument = null },
            icon = {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GalanaNavy, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(
                    text = "SAP Doc: ${doc.billingDocument} / Item ${doc.billingDocumentItem}",
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• Plant / Terminal: ${doc.plantName} (ID: ${doc.locationId})", fontSize = 13.sp)
                    Text("• Customer Account: ${doc.customerName} (${doc.customerId})", fontSize = 13.sp)
                    Text("• Customer Segment: ${doc.customerType}", fontSize = 13.sp)
                    Text("• Petroleum Grade: ${doc.productName} (${doc.productId})", fontSize = 13.sp)
                    Text("• Quantity Dispatched: ${currencyFormat.format(doc.quantity)} ${doc.salesUnit}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("• Gross Amount: ${doc.transactionCurrency} ${currencyFormat.format(doc.grossAmount)}", fontSize = 13.sp)
                    Text("• Commercial Discount: ${doc.transactionCurrency} ${currencyFormat.format(doc.discountAmount)}", fontSize = 13.sp, color = StatusWarning)
                    Text("• Net Billed Amount: ${doc.transactionCurrency} ${currencyFormat.format(doc.netAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GalanaNavy)
                    Text("• Document Date: ${doc.billingDocumentDate} • Sales Org: ${doc.salesOrganization}", fontSize = 12.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDocument = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Close Document")
                }
            }
        )
    }

    syncFeedbackMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { syncFeedbackMessage = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FuelPetrolGreen, modifier = Modifier.size(36.dp))
            },
            title = { Text("SAP Sync Complete", fontWeight = FontWeight.Bold, color = GalanaNavy) },
            text = { Text(msg, fontSize = 13.sp, color = TextSecondary) },
            confirmButton = {
                Button(onClick = { syncFeedbackMessage = null }, colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SapMetricCard(
    label: String,
    value: String,
    sublabel: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sublabel, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun SapBillingItemCard(
    item: SapBillingItem,
    currencyFormat: NumberFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sap_doc_${item.billingDocument}"),
        shape = RoundedCornerShape(14.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "#${item.billingDocument}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GalanaNavy
                    )
                    Text(
                        text = item.billingDocumentDate,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                Text(
                    text = item.plantName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.plantName.contains("Mombasa")) GalanaAmberDark else FuelDieselBlue
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.customerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${item.productName} • ${currencyFormat.format(item.quantity)} ${item.salesUnit}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.transactionCurrency} ${currencyFormat.format(item.netAmount)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = GalanaNavy
                    )
                    Text(
                        text = "Gross: ${currencyFormat.format(item.grossAmount)}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
