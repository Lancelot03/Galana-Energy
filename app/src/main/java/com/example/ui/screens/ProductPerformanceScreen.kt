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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EnergyRepository
import com.example.model.ProductItem
import com.example.model.ScreenRoute
import com.example.ui.theme.*

@Composable
fun ProductPerformanceScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedProductDetail by remember { mutableStateOf<ProductItem?>(null) }

    val filteredProducts = remember(selectedCategory) {
        when (selectedCategory) {
            "Fuel" -> EnergyRepository.products.filter { it.category == "Fuel" }
            "Lubricants" -> EnergyRepository.products.filter { it.category == "Lubricants" }
            else -> EnergyRepository.products
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
                            text = "Product Performance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Sep 2026 vs Aug", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Fuel", "Lubricants").forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Button(
                            onClick = { selectedCategory = cat },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) GalanaNavy else Color.White,
                                contentColor = if (isSelected) Color.White else TextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }

            // Summary Totals Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Volume", fontSize = 11.sp, color = TextSecondary)
                            Text("${filteredProducts.sumOf { it.volumeKL }} KL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GalanaNavy)
                        }
                        Divider(modifier = Modifier.height(28.dp).width(1.dp), color = Color(0xFFE2E8F0))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Revenue", fontSize = 11.sp, color = TextSecondary)
                            Text("KES ${String.format("%.2f", filteredProducts.sumOf { it.salesKES })}M", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GalanaNavy)
                        }
                        Divider(modifier = Modifier.height(28.dp).width(1.dp), color = Color(0xFFE2E8F0))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Avg Gross Margin", fontSize = 11.sp, color = TextSecondary)
                            Text("${String.format("%.1f", filteredProducts.map { it.grossMarginPercent }.average())}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FuelPetrolGreen)
                        }
                    }
                }
            }

            // Product Cards List
            items(filteredProducts) { product ->
                ProductCardItem(
                    product = product,
                    onClick = { selectedProductDetail = product }
                )
            }
        }
    }

    selectedProductDetail?.let { prod ->
        AlertDialog(
            onDismissRequest = { selectedProductDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(prod.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(getProductIcon(prod.iconType), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text(prod.name, fontWeight = FontWeight.Bold, color = GalanaNavy)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(prod.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                    Divider(color = Color(0xFFE2E8F0))
                    Text("• Monthly Dispatched Volume: ${prod.volumeKL} KL", fontSize = 13.sp)
                    Text("• Monthly Gross Sales: KES ${prod.salesKES}M (${if (prod.isPositive) "▲" else "▼"} ${prod.change}%)", fontSize = 13.sp)
                    Text("• Current Wholesale Unit Price: KES ${prod.unitPriceKES} / Unit", fontSize = 13.sp)
                    Text("• Operational Gross Margin: ${prod.grossMarginPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FuelPetrolGreen)
                    Text("• Primary Buyers: Commercial transport fleets, KenGen, KPA, and retail dealer networks.", fontSize = 12.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedProductDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ProductCardItem(
    product: ProductItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_item_${product.id}"),
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
                        .background(Color(product.colorHex).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getProductIcon(product.iconType),
                        contentDescription = null,
                        tint = Color(product.colorHex),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${product.volumeKL} KL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GalanaNavy
                        )
                        Text(
                            text = "• Margin: ${product.grossMarginPercent}%",
                            fontSize = 11.sp,
                            color = FuelPetrolGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "KES ${product.salesKES}M",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
                Text(
                    text = "${if (product.isPositive) "▲" else "▼"} ${product.change}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (product.isPositive) FuelPetrolGreen else StatusNegative
                )
            }
        }
    }
}

fun getProductIcon(iconType: String): ImageVector {
    return when (iconType) {
        "fuel" -> Icons.Default.LocalGasStation
        "canister" -> Icons.Default.OilBarrel
        "plane" -> Icons.Default.FlightTakeoff
        "oil" -> Icons.Default.Science
        "gear" -> Icons.Default.Settings
        "grease" -> Icons.Default.Build
        else -> Icons.Default.LocalGasStation
    }
}
