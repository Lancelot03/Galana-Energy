package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EnergyRepository
import com.example.model.ScreenRoute
import com.example.ui.components.TrendChart
import com.example.ui.theme.*

@Composable
fun SalesTrendScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf("value") } // "value", "volume", "margin"
    var selectedIndex by remember { mutableStateOf(EnergyRepository.dailyTrends.lastIndex) }

    val trends = EnergyRepository.dailyTrends
    val selectedDay = trends.getOrNull(selectedIndex)

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
                        text = "Sales Trend Analytics",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Metric Toggle Tabs: Value, Volume, Margin
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("value" to "Sales Value", "volume" to "Volume (KL)", "margin" to "Margin Rate").forEach { (key, label) ->
                        val isSelected = selectedMetric == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GalanaNavy else Color.Transparent)
                                .clickable { selectedMetric = key }
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

            // Interactive Trend Chart
            item {
                TrendChart(
                    trends = trends,
                    selectedMetric = selectedMetric,
                    selectedIndex = selectedIndex,
                    onSelectDay = { selectedIndex = it }
                )
            }

            // Executive Trend Insight Card
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
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = FuelPetrolGreen, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Weekly Trajectory Analysis",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                        }
                        Text(
                            text = "Peak daily dispatch achieved on 26 Sep (76 KL / KES 2.4M), propelled by concurrent bulk deliveries to KenGen Kipevu and Kenya Ports Authority. Gross margin expanded by 200 bps over the 7-day cycle due to optimized coastal import blend.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Daily Logs Table
            item {
                Text(
                    text = "Daily Dispatch & Revenue Ledger",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            }

            itemsIndexed(trends) { idx, item ->
                val isSelected = idx == selectedIndex
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = idx }
                        .testTag("trend_item_${item.day}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GalanaNavy else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.day.take(2),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else GalanaNavy
                                )
                            }
                            Column {
                                Text(item.day, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${item.volumeKL} KL Dispatched", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "KES ${item.value}M",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GalanaNavy
                            )
                            Text(
                                text = "Margin: ${item.marginPercent}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FuelPetrolGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
