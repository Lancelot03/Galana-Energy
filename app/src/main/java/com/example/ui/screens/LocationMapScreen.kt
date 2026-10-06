package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EnergyRepository
import com.example.model.LocationItem
import com.example.model.ScreenRoute
import com.example.ui.components.KenyaMapCanvas
import com.example.ui.theme.*

@Composable
fun LocationMapScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedLocation by remember {
        mutableStateOf(EnergyRepository.locations.find { it.id == "mombasa-depot" } ?: EnergyRepository.locations.first())
    }
    var viewMode by remember { mutableStateOf("map") } // "map" or "list"

    val filteredLocations = remember(selectedFilter) {
        when (selectedFilter) {
            "Depots" -> EnergyRepository.locations.filter { it.type == "Depot" }
            "Stations" -> EnergyRepository.locations.filter { it.type == "Station" }
            else -> EnergyRepository.locations
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
                        Text(
                            text = "Sales by Location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onNavigate(ScreenRoute.ALL_LOCATIONS) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = "All Locations", tint = Color.White, modifier = Modifier.size(18.dp))
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
            // View Mode & Type Filter Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter Chips: All, Depots, Stations
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All", "Depots", "Stations").forEach { chip ->
                            val isSelected = selectedFilter == chip
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = chip },
                                label = {
                                    Text(
                                        text = if (chip == "Depots") "Depots (3)" else if (chip == "Stations") "Stations (23)" else "All (28)",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GalanaNavy,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White
                                )
                            )
                        }
                    }

                    // Map vs List toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = { viewMode = "map" },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (viewMode == "map") Color.White else Color.Transparent)
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = "Map view",
                                tint = if (viewMode == "map") GalanaNavy else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewMode = "list" },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (viewMode == "list") Color.White else Color.Transparent)
                        ) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "List view",
                                tint = if (viewMode == "list") GalanaNavy else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (viewMode == "map") {
                // Interactive Map Canvas
                item {
                    KenyaMapCanvas(
                        locations = filteredLocations,
                        selectedLocation = selectedLocation,
                        onSelectLocation = { selectedLocation = it },
                        onOpenDetails = { loc ->
                            if (loc.id == "mombasa-depot") {
                                onNavigate(ScreenRoute.MOMBASA_DEPOT)
                            } else {
                                onNavigate(ScreenRoute.ALL_LOCATIONS)
                            }
                        }
                    )
                }
            }

            // Locations List
            item {
                Text(
                    text = if (viewMode == "map") "Network Hubs & Performance" else "All Locations Directory",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            }

            items(filteredLocations) { loc ->
                LocationCardItem(
                    location = loc,
                    isSelected = selectedLocation.id == loc.id,
                    onClick = {
                        selectedLocation = loc
                        if (loc.id == "mombasa-depot") {
                            onNavigate(ScreenRoute.MOMBASA_DEPOT)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LocationCardItem(
    location: LocationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("location_item_${location.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F7FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (location.type == "Depot") GalanaAmber.copy(alpha = 0.15f)
                            else FuelDieselBlue.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (location.type == "Depot") Icons.Default.Warehouse else Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = if (location.type == "Depot") GalanaAmberDark else FuelDieselBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = location.region,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text = "${location.volumeKL} KL Dispatched • ${location.status}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "KES ${location.salesKES}M",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
                Text(
                    text = "${if (location.isPositive) "▲" else "▼"} ${location.change}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (location.isPositive) FuelPetrolGreen else StatusNegative
                )
            }
        }
    }
}
