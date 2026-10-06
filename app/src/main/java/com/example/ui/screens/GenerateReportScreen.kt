package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.theme.*

@Composable
fun GenerateReportScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedReportType by remember { mutableStateOf("sales_summary") }
    var selectedPeriod by remember { mutableStateOf("Sep 2026") }
    var selectedRegion by remember { mutableStateOf("All Regions") }
    var selectedProductCategory by remember { mutableStateOf("All Products") }
    var showSuccessDialog by remember { mutableStateOf<String?>(null) }

    val reportTypes = listOf(
        ReportTypeItem("sales_summary", "Sales Summary", Icons.Default.Assessment),
        ReportTypeItem("location_perf", "Location Performance", Icons.Default.Place),
        ReportTypeItem("product_perf", "Product Performance", Icons.Default.Inventory2),
        ReportTypeItem("customer_sales", "Customer Sales", Icons.Default.CorporateFare),
        ReportTypeItem("daily_sales", "Daily Dispatch Log", Icons.Default.CalendarToday),
        ReportTypeItem("monthly_trend", "Monthly Trend Analysis", Icons.Default.TrendingUp),
        ReportTypeItem("margin_discount", "Margin & Discounts", Icons.Default.PriceCheck),
        ReportTypeItem("receivables", "Receivables & Credit", Icons.Default.AccountBalanceWallet)
    )

    Scaffold(
        topBar = {
            Surface(color = GalanaNavy, shadowElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Executive Report Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
            // Report Type Selection
            item {
                Text(
                    text = "Select Report Template",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reportTypes.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                val isSelected = selectedReportType == item.id
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedReportType = item.id },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) GalanaNavy else Color(0xFFF1F5F9)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else GalanaNavy,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GalanaNavy else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Scope & Filters Configuration
            item {
                Text(
                    text = "Report Scope & Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReportFilterSelector(
                            label = "Reporting Period",
                            currentValue = selectedPeriod,
                            options = listOf("Sep 2026", "Aug 2026", "Q3 2026", "Year-to-Date 2026"),
                            onSelect = { selectedPeriod = it }
                        )

                        Divider(color = Color(0xFFF1F5F9))

                        ReportFilterSelector(
                            label = "Geographic Region",
                            currentValue = selectedRegion,
                            options = listOf("All Regions", "Coast", "Nairobi", "Western", "Rift Valley"),
                            onSelect = { selectedRegion = it }
                        )

                        Divider(color = Color(0xFFF1F5F9))

                        ReportFilterSelector(
                            label = "Product Category",
                            currentValue = selectedProductCategory,
                            options = listOf("All Products", "Fuels Only", "Lubricants Only"),
                            onSelect = { selectedProductCategory = it }
                        )
                    }
                }
            }

            // Report Preview Digest Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E36))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXECUTIVE SUMMARY PREVIEW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GalanaAmberLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Confidential",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Text(
                            text = "Galana Energy Kenya • ${reportTypes.find { it.id == selectedReportType }?.label ?: "Sales"} ($selectedPeriod)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Includes consolidated off-take figures for Mombasa Terminal, 28 retail/depot branches, key bulk accounts (KenGen, KPA, KQ), and margin variances.",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Gross: KES 312.5M", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Volume: 10,850 KL", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Margin: 14.1%", fontSize = 11.sp, color = FuelPetrolGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Export Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            showSuccessDialog = "Executive PDF Report successfully compiled and queued for download."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_pdf_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Executive PDF", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            showSuccessDialog = "Spreadsheet CSV compiled with all granular line items and transactional fields."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_csv_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = GalanaNavy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export CSV Spreadsheet", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = GalanaNavy)
                    }
                }
            }
        }
    }

    showSuccessDialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { showSuccessDialog = null },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FuelPetrolGreen, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("Report Export Successful", fontWeight = FontWeight.Bold, color = GalanaNavy)
            },
            text = {
                Text(msg, fontSize = 13.sp, color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

data class ReportTypeItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun ReportFilterSelector(
    label: String,
    currentValue: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF8FAFC))
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentValue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                options.forEach { opt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(opt)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = opt,
                            fontSize = 12.sp,
                            fontWeight = if (opt == currentValue) FontWeight.Bold else FontWeight.Normal,
                            color = if (opt == currentValue) GalanaNavy else TextPrimary
                        )
                    }
                }
            }
        }
    }
}
