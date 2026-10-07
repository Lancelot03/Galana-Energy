package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalanaApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(ScreenRoute.HOME) }
    var screenStack by remember { mutableStateOf(listOf(ScreenRoute.HOME)) }
    var showMoreSheet by remember { mutableStateOf(false) }

    fun navigateTo(route: ScreenRoute) {
        showMoreSheet = false
        if (currentScreen != route) {
            screenStack = screenStack + route
            currentScreen = route
        }
    }

    fun navigateBack() {
        showMoreSheet = false
        if (screenStack.size > 1) {
            val newStack = screenStack.dropLast(1)
            screenStack = newStack
            currentScreen = newStack.last()
        } else if (currentScreen != ScreenRoute.HOME && currentScreen != ScreenRoute.SIGN_IN) {
            currentScreen = ScreenRoute.HOME
            screenStack = listOf(ScreenRoute.HOME)
        }
    }

    // Handle back button on all screens
    BackHandler(enabled = currentScreen != ScreenRoute.HOME && currentScreen != ScreenRoute.SIGN_IN) {
        navigateBack()
    }

    val showBottomBar = currentScreen != ScreenRoute.SIGN_IN

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = if (currentScreen == ScreenRoute.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            label = "Home",
                            isSelected = currentScreen == ScreenRoute.HOME,
                            onClick = { navigateTo(ScreenRoute.HOME) },
                            testTag = "nav_home"
                        )
                        BottomNavItem(
                            icon = if (currentScreen == ScreenRoute.SALES_TREND) Icons.Filled.Timeline else Icons.Outlined.Timeline,
                            label = "Sales",
                            isSelected = currentScreen == ScreenRoute.SALES_TREND,
                            onClick = { navigateTo(ScreenRoute.SALES_TREND) },
                            testTag = "nav_sales"
                        )
                        BottomNavItem(
                            icon = if (currentScreen in listOf(ScreenRoute.LOCATION_MAP, ScreenRoute.ALL_LOCATIONS, ScreenRoute.MOMBASA_DEPOT)) Icons.Filled.Map else Icons.Outlined.Map,
                            label = "Locations",
                            isSelected = currentScreen in listOf(ScreenRoute.LOCATION_MAP, ScreenRoute.ALL_LOCATIONS, ScreenRoute.MOMBASA_DEPOT),
                            onClick = { navigateTo(ScreenRoute.LOCATION_MAP) },
                            testTag = "nav_locations"
                        )
                        BottomNavItem(
                            icon = if (currentScreen == ScreenRoute.DATA_UPLOAD) Icons.Filled.CloudUpload else Icons.Outlined.CloudUpload,
                            label = "Upload",
                            isSelected = currentScreen == ScreenRoute.DATA_UPLOAD,
                            onClick = { navigateTo(ScreenRoute.DATA_UPLOAD) },
                            testTag = "nav_upload"
                        )
                        BottomNavItem(
                            icon = if (currentScreen == ScreenRoute.PRODUCT_PERFORMANCE) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                            label = "Products",
                            isSelected = currentScreen == ScreenRoute.PRODUCT_PERFORMANCE,
                            onClick = { navigateTo(ScreenRoute.PRODUCT_PERFORMANCE) },
                            testTag = "nav_products"
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.MoreHoriz,
                            label = "More",
                            isSelected = showMoreSheet,
                            onClick = { showMoreSheet = true },
                            testTag = "nav_more"
                        )
                    }
                }
            }
        },
        containerColor = BackgroundLight,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (currentScreen) {
                ScreenRoute.SIGN_IN -> SignInScreen(
                    onSignInSuccess = {
                        screenStack = listOf(ScreenRoute.HOME)
                        currentScreen = ScreenRoute.HOME
                    }
                )
                ScreenRoute.HOME -> HomeScreen(
                    onNavigate = { navigateTo(it) },
                    onSignOut = {
                        screenStack = listOf(ScreenRoute.SIGN_IN)
                        currentScreen = ScreenRoute.SIGN_IN
                    }
                )
                ScreenRoute.LOCATION_MAP -> LocationMapScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.MOMBASA_DEPOT -> MombasaDepotScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.PRODUCT_PERFORMANCE -> ProductPerformanceScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.ALL_LOCATIONS -> AllLocationsScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.SALES_TREND -> SalesTrendScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.KEY_CUSTOMERS -> KeyCustomersScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.GENERATE_REPORT -> GenerateReportScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.ASK_AI -> AskAiScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.SAP_LIVE_SYNC -> SapLiveSyncScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
                ScreenRoute.DATA_UPLOAD -> DataUploadScreen(
                    onNavigate = { navigateTo(it) },
                    onBack = { navigateBack() }
                )
            }
        }
    }

    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Executive Management Modules",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                MoreSheetItem(
                    title = "Mombasa Depot 360",
                    subtitle = "Terminal Berth 1, Tank levels & Gantries",
                    icon = Icons.Default.Visibility,
                    iconColor = GalanaAmberDark,
                    onClick = { navigateTo(ScreenRoute.MOMBASA_DEPOT) }
                )

                MoreSheetItem(
                    title = "Key Corporate Accounts",
                    subtitle = "KenGen, KPA, KQ and wholesale buyers",
                    icon = Icons.Default.CorporateFare,
                    iconColor = FuelDieselBlue,
                    onClick = { navigateTo(ScreenRoute.KEY_CUSTOMERS) }
                )

                MoreSheetItem(
                    title = "Executive Report Generator",
                    subtitle = "Compile and export PDF & CSV briefs",
                    icon = Icons.Default.Assessment,
                    iconColor = Color(0xFF475569),
                    onClick = { navigateTo(ScreenRoute.GENERATE_REPORT) }
                )

                MoreSheetItem(
                    title = "Galana AI Copilot",
                    subtitle = "Gemini powered downstream petroleum analytics",
                    icon = Icons.Default.AutoAwesome,
                    iconColor = GalanaAmber,
                    onClick = { navigateTo(ScreenRoute.ASK_AI) }
                )

                MoreSheetItem(
                    title = "SAP Live Sync & Ledger",
                    subtitle = "S/4HANA OData Client 080 Billing Items",
                    icon = Icons.Default.CloudSync,
                    iconColor = FuelDieselBlue,
                    onClick = { navigateTo(ScreenRoute.SAP_LIVE_SYNC) }
                )

                MoreSheetItem(
                    title = "Upload Excel / JSON Data",
                    subtitle = "Ingest custom sales data & demo presets",
                    icon = Icons.Default.CloudUpload,
                    iconColor = GalanaAmber,
                    onClick = { navigateTo(ScreenRoute.DATA_UPLOAD) }
                )

                Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

                MoreSheetItem(
                    title = "Sign Out",
                    subtitle = "Securely end executive session",
                    icon = Icons.Default.Logout,
                    iconColor = StatusNegative,
                    onClick = {
                        showMoreSheet = false
                        currentScreen = ScreenRoute.SIGN_IN
                        screenStack = listOf(ScreenRoute.SIGN_IN)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GalanaNavy else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GalanaNavy else TextMuted
        )
    }
}

@Composable
fun MoreSheetItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
