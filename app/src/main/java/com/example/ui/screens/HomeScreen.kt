package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EnergyRepository
import com.example.data.SapSalesRepository
import com.example.model.ActiveDataSource
import com.example.model.ScreenRoute
import com.example.model.Timeframe
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val activeDataSource by SapSalesRepository.activeDataSource.collectAsState()
    var selectedTimeframe by remember { mutableStateOf(Timeframe.TODAY) }
    val kpi = remember(selectedTimeframe, billingItems) { EnergyRepository.computeKpis(selectedTimeframe, billingItems) }
    val regionalSales = remember(billingItems) { EnergyRepository.computeRegionalSales(billingItems) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Executive Top Bar
        item {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.galana_icon_1791266706994),
                            contentDescription = "Galana Energy",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = "GALANA ENERGY",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = GalanaNavy,
                                letterSpacing = 0.5.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FuelPetrolGreen)
                                )
                                Text(
                                    text = "Live Operations • Sep 2026",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { showNotificationDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = GalanaAmber) {
                                        Text("3", fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = GalanaNavy
                                )
                            }
                        }

                        IconButton(
                            onClick = { onNavigate(ScreenRoute.ASK_AI) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Ask AI",
                                tint = GalanaAmberDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Data Source & SAP Integration Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigate(ScreenRoute.DATA_UPLOAD) }
                    .testTag("sap_live_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (activeDataSource) {
                        is ActiveDataSource.FileUploaded -> Color(0xFF0F2E1E)
                        is ActiveDataSource.LiveSapApi -> Color(0xFF0D253F)
                        is ActiveDataSource.StaticDefault -> Color(0xFF0F1E36)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (activeDataSource) {
                                    is ActiveDataSource.FileUploaded -> Icons.Default.Description
                                    is ActiveDataSource.LiveSapApi -> Icons.Default.CloudSync
                                    is ActiveDataSource.StaticDefault -> Icons.Default.Storage
                                },
                                contentDescription = null,
                                tint = when (activeDataSource) {
                                    is ActiveDataSource.FileUploaded -> Color(0xFF86EFAC)
                                    is ActiveDataSource.LiveSapApi -> Color(0xFF38BDF8)
                                    is ActiveDataSource.StaticDefault -> FuelPetrolGreen
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = when (val src = activeDataSource) {
                                        is ActiveDataSource.FileUploaded -> "CUSTOM FILE: ${src.fileName.take(18)}"
                                        is ActiveDataSource.LiveSapApi -> "LIVE SAP S/4HANA ODATA"
                                        is ActiveDataSource.StaticDefault -> "SAP S/4HANA BASELINE"
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
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FuelPetrolGreen)
                                )
                            }
                            Text(
                                text = when (val src = activeDataSource) {
                                    is ActiveDataSource.FileUploaded -> "${src.recordCount} records loaded • Real-time Active"
                                    is ActiveDataSource.LiveSapApi -> "Client 080 • ZANI_UI_GAL_SALES • ${billingItems.size} items"
                                    is ActiveDataSource.StaticDefault -> "Client 080 • ${billingItems.size} baseline records (KES 11.64M)"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (activeDataSource is ActiveDataSource.FileUploaded) "Manage" else "Upload/Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Timeframe Selector Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Timeframe.values().forEach { tf ->
                    val isSelected = selectedTimeframe == tf
                    val label = when (tf) {
                        Timeframe.TODAY -> "Today (${EnergyRepository.computeKpis(Timeframe.TODAY, billingItems).total})"
                        Timeframe.WEEK -> "Week (${EnergyRepository.computeKpis(Timeframe.WEEK, billingItems).total})"
                        Timeframe.MONTH -> "Month (${EnergyRepository.computeKpis(Timeframe.MONTH, billingItems).total})"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GalanaNavy else Color.Transparent)
                            .clickable { selectedTimeframe = tf }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        // 4 Primary KPI Cards Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricCard(
                        title = "Total Sales Value",
                        value = "KES ${kpi.total}",
                        change = "▲ ${kpi.totalChange}",
                        isPositive = true,
                        accentColor = GalanaNavy,
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.SALES_TREND) }
                    )
                    KpiMetricCard(
                        title = "Fuel Revenue",
                        value = "KES ${kpi.fuel}",
                        change = "▲ ${kpi.fuelChange}",
                        isPositive = true,
                        accentColor = FuelDieselBlue,
                        icon = Icons.Default.LocalGasStation,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.PRODUCT_PERFORMANCE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricCard(
                        title = "Lubricants",
                        value = "KES ${kpi.lube}",
                        change = "▲ ${kpi.lubeChange}",
                        isPositive = true,
                        accentColor = LubePurple,
                        icon = Icons.Default.Science,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.PRODUCT_PERFORMANCE) }
                    )
                    KpiMetricCard(
                        title = "Volume Dispatched",
                        value = "${kpi.volume} KL",
                        change = "▲ ${kpi.volChange}",
                        isPositive = true,
                        accentColor = GalanaAmberDark,
                        icon = Icons.Default.Speed,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.SALES_TREND) }
                    )
                }
            }
        }

        // Featured Hero Card: Mombasa Terminal 360
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigate(ScreenRoute.MOMBASA_DEPOT) }
                    .testTag("mombasa_terminal_hero_card"),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mombasa_terminal_1791266720796),
                            contentDescription = "Mombasa Oil Terminal & Depots",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xCC0F1E36)),
                                        startY = 60f
                                    )
                                )
                        )

                        // 360 Badge
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xCC0F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "360° Terminal View",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Bottom caption on image
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "MOMBASA STRATEGIC TERMINAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GalanaAmberLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Kipevu Oil Storage Terminal Link • Berth 1 & 2",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    // Terminal Live Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Storage Capacity", fontSize = 10.sp, color = TextSecondary)
                                Text("84% Full", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GalanaNavy)
                            }
                            Column {
                                Text("Loading Gantries", fontSize = 10.sp, color = TextSecondary)
                                Text("12 / 12 Active", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FuelPetrolGreen)
                            }
                            Column {
                                Text("Avg Turnaround", fontSize = 10.sp, color = TextSecondary)
                                Text("34 mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GalanaAmberDark)
                            }
                        }

                        Button(
                            onClick = { onNavigate(ScreenRoute.MOMBASA_DEPOT) },
                            colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Open", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Executive Modules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleShortcutCard(
                        title = "Kenya Map",
                        subtitle = "28 Stations & Depots",
                        icon = Icons.Default.Map,
                        color = FuelDieselBlue,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.LOCATION_MAP) }
                    )
                    ModuleShortcutCard(
                        title = "Sales Trends",
                        subtitle = "Daily & Monthly Logs",
                        icon = Icons.Default.Timeline,
                        color = GalanaAmberDark,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.SALES_TREND) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleShortcutCard(
                        title = "Products",
                        subtitle = "Fuel & Lubricants",
                        icon = Icons.Default.Inventory2,
                        color = FuelPetrolGreen,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.PRODUCT_PERFORMANCE) }
                    )
                    ModuleShortcutCard(
                        title = "Key Customers",
                        subtitle = "KenGen, KPA, KQ",
                        icon = Icons.Default.CorporateFare,
                        color = LubePurple,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.KEY_CUSTOMERS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleShortcutCard(
                        title = "Executive Reports",
                        subtitle = "PDF & CSV Export",
                        icon = Icons.Default.Assessment,
                        color = Color(0xFF475569),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.GENERATE_REPORT) }
                    )
                    ModuleShortcutCard(
                        title = "Ask AI Copilot",
                        subtitle = "Energy Intelligence",
                        icon = Icons.Default.AutoAwesome,
                        color = GalanaAmber,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.ASK_AI) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleShortcutCard(
                        title = "SAP Live Sync",
                        subtitle = "OData Billing Items",
                        icon = Icons.Default.CloudSync,
                        color = Color(0xFF0369A1),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.SAP_LIVE_SYNC) }
                    )
                    ModuleShortcutCard(
                        title = "Mombasa 360",
                        subtitle = "Tank Farm & Gantries",
                        icon = Icons.Default.Visibility,
                        color = GalanaAmberDark,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.MOMBASA_DEPOT) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleShortcutCard(
                        title = "Upload Data",
                        subtitle = "Excel & JSON Ingestion",
                        icon = Icons.Default.CloudUpload,
                        color = GalanaAmber,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.DATA_UPLOAD) }
                    )
                    ModuleShortcutCard(
                        title = "All Locations",
                        subtitle = "28 Station Directory",
                        icon = Icons.Default.FormatListBulleted,
                        color = Color(0xFF475569),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(ScreenRoute.ALL_LOCATIONS) }
                    )
                }
            }
        }

        // Regional Revenue Breakdown Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Regional Sales Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GalanaNavy
                        )
                        Text(
                            text = "Sep 2026",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    regionalSales.forEach { reg ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${reg.region} (${reg.stationsCount} hubs)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "KES ${reg.salesKES}M",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GalanaNavy
                                    )
                                    Text(
                                        text = "${if (reg.isPositive) "▲" else "▼"} ${reg.change}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (reg.isPositive) FuelPetrolGreen else StatusNegative
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            val progressFraction = (reg.salesKES / 6.0).toFloat().coerceIn(0.05f, 1f)
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (reg.isPositive) GalanaNavy else StatusNegative,
                                trackColor = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Operations Dispatch Center", fontWeight = FontWeight.Bold, color = GalanaNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("• Tanker MT Kilindini Pride docked at KOT Berth 1 (Offloading 25,000 KL AGO Diesel).", fontSize = 13.sp)
                    Text("• Mombasa Depot achieved 12/12 active gantry lanes with zero safety downtime.", fontSize = 13.sp)
                    Text("• KenGen Kipevu monthly procurement allocation fulfilled at 100%.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Acknowledge")
                }
            }
        )
    }
}

@Composable
fun KpiMetricCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = change,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) FuelPetrolGreen else StatusNegative
            )
        }
    }
}

@Composable
fun ModuleShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
