package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EnergyRepository
import com.example.data.SapSalesRepository
import com.example.model.ScreenRoute
import com.example.ui.components.DepotTankGauge
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MombasaDepotScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val mombasaTanks = remember(billingItems) { EnergyRepository.computeMombasaTanks(billingItems) }

    val mombasaItems = remember(billingItems) {
        billingItems.filter { it.plantName.contains("Mombasa", ignoreCase = true) }
    }
    val mombasaTotalNetM = remember(mombasaItems) {
        if (mombasaItems.isNotEmpty()) mombasaItems.sumOf { it.netAmount } / 1_000_000.0 else 2.84
    }
    val mombasaFuelNetM = remember(mombasaItems) {
        if (mombasaItems.isNotEmpty()) mombasaItems.filter { it.productCategory.equals("Fuel", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0 else 2.31
    }
    val mombasaLubeNetM = remember(mombasaItems) {
        if (mombasaItems.isNotEmpty()) mombasaItems.filter { it.productCategory.equals("Lubricants", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0 else 0.53
    }

    var selectedTab by remember { mutableStateOf("overview") }
    var show360Viewer by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 1.dp) {
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Text(
                            text = "Mombasa Depot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFD1FAE5))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Active • Berth 1 Link",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
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
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Photo of Terminal
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mombasa_terminal_1791266720796),
                        contentDescription = "Mombasa Oil Terminal & Depots",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xCC0F1E36)),
                                    startY = 80f
                                )
                            )
                    )

                    // 360 Action Pill
                    Button(
                        onClick = { show360Viewer = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC0F172A)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("360° Terminal Mode", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Kipevu Oil Storage Terminal Link • Berth 1 & 2",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Kilindini Harbor Coastal Deepwater Facility",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // Sub-tabs navigation
            item {
                Surface(color = Color.White, shadowElevation = 1.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("overview", "products", "trends", "tanks").forEach { tab ->
                            val isSelected = selectedTab == tab
                            Box(
                                modifier = Modifier
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 12.dp, horizontal = 14.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when (tab) {
                                            "overview" -> "Overview"
                                            "products" -> "Products"
                                            "trends" -> "Trends"
                                            else -> "Gantries & Tanks"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) GalanaNavy else TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(2.5.dp)
                                            .width(28.dp)
                                            .background(if (isSelected) GalanaNavy else Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main KPI Cards
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date & Location Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "25 Sep 2026 • Live Shift",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GalanaNavy
                        )
                        Text(
                            text = "Coast Regional Hub",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    // 3-Metric Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DepotKpiMiniCard(
                            label = "Total Sales",
                            value = "KES ${String.format(Locale.US, "%.2f", mombasaTotalNetM)}M",
                            change = "▲ 12.6%",
                            modifier = Modifier.weight(1f)
                        )
                        DepotKpiMiniCard(
                            label = "Fuel",
                            value = "KES ${String.format(Locale.US, "%.2f", mombasaFuelNetM)}M",
                            change = "▲ 13.2%",
                            modifier = Modifier.weight(1f)
                        )
                        DepotKpiMiniCard(
                            label = "Lubricants",
                            value = "KES ${String.format(Locale.US, "%.2f", mombasaLubeNetM)}M",
                            change = "▲ 9.1%",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Facility Status Strip
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Storage, contentDescription = null, tint = FuelDieselBlue, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text("84% Storage Capacity", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Tank 1, 2, 4 Full • Tank 3 Intake", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Divider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp),
                                color = Color(0xFFE2E8F0)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFECFDF5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FuelPetrolGreen, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text("12 / 12 Gantries", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("All Automated Bays", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Tab-specific views
            when (selectedTab) {
                "tanks" -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Petroleum Tank Farm Storage Levels",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(mombasaTanks) { tank ->
                                    DepotTankGauge(tank = tank)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Gantry Automated Bays Monitor
                            Text(
                                text = "Automated Loading Racks (Gantries 1-12)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    for (row in 0..2) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            for (col in 1..4) {
                                                val bayNum = row * 4 + col
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (bayNum in listOf(3, 7)) Color(0xFFFEF3C7) else Color(0xFFD1FAE5))
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "Bay $bayNum • ${if (bayNum in listOf(3, 7)) "Loading" else "Ready"}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (bayNum in listOf(3, 7)) Color(0xFFB45309) else Color(0xFF065F46)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Top Products in Mombasa
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Top Products Dispatched",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = GalanaNavy
                                    )
                                    TextButton(onClick = { onNavigate(ScreenRoute.PRODUCT_PERFORMANCE) }) {
                                        Text("View all", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GalanaNavyLight)
                                    }
                                }

                                MombasaProductRow(name = "Diesel (AGO)", volume = "42 KL", sales = "KES 1.26M", percentage = 0.65f, color = FuelDieselBlue)
                                MombasaProductRow(name = "Super Petrol (PMS)", volume = "28 KL", sales = "KES 0.84M", percentage = 0.45f, color = FuelPetrolGreen)
                                MombasaProductRow(name = "Heavy Duty Lube", volume = "8 KL", sales = "KES 0.21M", percentage = 0.20f, color = GalanaAmber)
                            }
                        }
                    }

                    // Maritime Tanker Intake & Pipeline Offtake Update
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.DirectionsBoat, contentDescription = null, tint = GalanaAmberLight, modifier = Modifier.size(18.dp))
                                    Text("Berth 1 Vessel Logistics", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Text(
                                    text = "Vessel 'MT Southern Star' completed discharging 18,500 KL Jet A-1 into Tank 3 with zero manifold loss. Line 4 to KPC pipeline booster is in pressurized standby.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (show360Viewer) {
        AlertDialog(
            onDismissRequest = { show360Viewer = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = GalanaNavy)
                    Text("Mombasa 360° Terminal View", fontWeight = FontWeight.Bold, color = GalanaNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mombasa_terminal_1791266720796),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text("Interactive terminal vantage looking out over Kilindini Channel, KOT Berth 1, and the automated 12-rack gantry loading yard.", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { show360Viewer = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Close 360° View")
                }
            }
        )
    }
}

@Composable
fun DepotKpiMiniCard(
    label: String,
    value: String,
    change: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(change, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FuelPetrolGreen)
        }
    }
}

@Composable
fun MombasaProductRow(
    name: String,
    volume: String,
    sales: String,
    percentage: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(volume, fontSize = 12.sp, color = TextSecondary)
                Text(sales, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}
