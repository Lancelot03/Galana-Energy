package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.SapSalesRepository
import com.example.model.LocationItem
import com.example.model.ScreenRoute
import com.example.ui.theme.*

@Composable
fun AllLocationsScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val allLocations = remember(billingItems) { EnergyRepository.computeLocations(billingItems) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All") }
    var selectedLocationDetail by remember { mutableStateOf<LocationItem?>(null) }

    val regions = listOf("All", "Coast", "Nairobi", "Western", "Rift Valley", "Eastern")

    val filtered = remember(searchQuery, selectedRegion, allLocations) {
        allLocations.filter { loc ->
            val matchesSearch = loc.name.contains(searchQuery, ignoreCase = true) ||
                    loc.region.contains(searchQuery, ignoreCase = true)
            val matchesRegion = selectedRegion == "All" || loc.region == selectedRegion
            matchesSearch && matchesRegion
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "All Locations Directory",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search location or county...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("location_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Region Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        regions.take(4).forEach { reg ->
                            val isSelected = selectedRegion == reg
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRegion = reg },
                                label = { Text(reg, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GalanaNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} Locations Found",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "Total: KES ${String.format("%.2f", filtered.sumOf { it.salesKES })}M",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            }

            // List of locations
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered) { loc ->
                    LocationCardItem(
                        location = loc,
                        isSelected = false,
                        onClick = {
                            if (loc.id == "mombasa-depot") {
                                onNavigate(ScreenRoute.MOMBASA_DEPOT)
                            } else {
                                selectedLocationDetail = loc
                            }
                        }
                    )
                }
            }
        }
    }

    selectedLocationDetail?.let { loc ->
        AlertDialog(
            onDismissRequest = { selectedLocationDetail = null },
            title = {
                Text(
                    text = "${loc.name} (${loc.type})",
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Region: ${loc.region} Commercial Corridor", fontSize = 13.sp)
                    Text("• Sales Revenue: KES ${loc.salesKES}M (${if (loc.isPositive) "▲" else "▼"} ${loc.change}% MoM)", fontSize = 13.sp)
                    Text("• Dispatched Volume: ${loc.volumeKL} KL", fontSize = 13.sp)
                    Text("• Operational State: ${loc.status}", fontSize = 13.sp)
                    Text("• Network Node: Connected to KPC Western pipeline / road tanker logistics.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedLocationDetail = null
                        onNavigate(ScreenRoute.LOCATION_MAP)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("View on Map")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLocationDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}
