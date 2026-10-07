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
import com.example.model.CustomerItem
import com.example.model.ScreenRoute
import com.example.ui.theme.*

@Composable
fun KeyCustomersScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val billingItems by SapSalesRepository.billingItems.collectAsState()
    val allCustomers = remember(billingItems) { EnergyRepository.computeCustomers(billingItems) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedCustomerDetail by remember { mutableStateOf<CustomerItem?>(null) }

    val filteredCustomers = remember(searchQuery, selectedCategory, allCustomers) {
        allCustomers.filter { cust ->
            val matchesSearch = cust.name.contains(searchQuery, ignoreCase = true) ||
                    cust.code.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == "All" || cust.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCat
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
                        text = "Key Corporate Customers",
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
            // Search & Category Filter Section
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search corporate account...", fontSize = 13.sp) },
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
                            .testTag("customer_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Industrial", "Fleet", "Wholesalers").forEach { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GalanaNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Results count banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredCustomers.size} Active Corporate Accounts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "KES ${String.format("%.2f", filteredCustomers.sumOf { it.salesKES })}M",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            }

            // Customer Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCustomers) { cust ->
                    CustomerCardItem(
                        customer = cust,
                        onClick = { selectedCustomerDetail = cust }
                    )
                }
            }
        }
    }

    selectedCustomerDetail?.let { cust ->
        AlertDialog(
            onDismissRequest = { selectedCustomerDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(cust.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cust.code, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Text(cust.name, fontWeight = FontWeight.Bold, color = GalanaNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Sector: ${cust.category} Wholesale Supply", fontSize = 13.sp)
                    Text("• Monthly Purchase: KES ${cust.salesKES}M (${if (cust.isPositive) "▲" else "▼"} ${cust.change}%)", fontSize = 13.sp)
                    Text("• Approved Credit Limit: ${cust.creditLimitKES}", fontSize = 13.sp)
                    Text("• Payment Performance: ${cust.paymentPerformance}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = FuelPetrolGreen)
                    Text("• Active Contracts: ${cust.contractsActive} Long-term supply agreements.", fontSize = 13.sp)
                    Text("• Primary Offtake: Mombasa Terminal Bulk Gantry & Pipeline Line 4.", fontSize = 12.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedCustomerDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Close Account Details")
                }
            }
        )
    }
}

@Composable
fun CustomerCardItem(
    customer: CustomerItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("customer_item_${customer.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(customer.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.code,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Column {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = customer.category,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "• ${customer.paymentPerformance}",
                            fontSize = 10.sp,
                            color = FuelPetrolGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "KES ${customer.salesKES}M",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
                Text(
                    text = "${if (customer.isPositive) "▲" else "▼"} ${customer.change}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (customer.isPositive) FuelPetrolGreen else StatusNegative
                )
            }
        }
    }
}
