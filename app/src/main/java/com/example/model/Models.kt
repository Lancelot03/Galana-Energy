package com.example.model

data class LocationItem(
    val id: String,
    val name: String,
    val type: String, // "Depot" or "Station"
    val region: String, // "Coast", "Nairobi", "Western", "Rift Valley", "Eastern"
    val salesKES: Double, // in Millions
    val change: Double, // percentage
    val isPositive: Boolean,
    val coordsX: Float, // relative 0..100
    val coordsY: Float, // relative 0..100
    val volumeKL: Int,
    val highlight: Boolean = false,
    val status: String = "Operational"
)

data class ProductItem(
    val id: String,
    val name: String,
    val category: String, // "Fuel" or "Lubricants"
    val volumeKL: Int,
    val salesKES: Double, // in Millions
    val change: Double,
    val isPositive: Boolean,
    val colorHex: Long,
    val iconType: String, // "fuel", "canister", "plane", "oil", "gear", "grease"
    val unitPriceKES: Double,
    val grossMarginPercent: Double,
    val description: String = ""
)

data class CustomerItem(
    val id: String,
    val name: String,
    val category: String, // "Industrial", "Fleet", "Wholesalers"
    val salesKES: Double, // in Millions
    val change: Double,
    val isPositive: Boolean,
    val code: String,
    val colorHex: Long,
    val contractsActive: Int = 2,
    val creditLimitKES: String = "KES 15M",
    val paymentPerformance: String = "100% On-Time"
)

data class RegionalSales(
    val region: String,
    val salesKES: Double,
    val change: Double,
    val isPositive: Boolean,
    val stationsCount: Int
)

data class DailyTrend(
    val day: String,
    val value: Double, // in Millions KES
    val volumeKL: Int,
    val marginPercent: Double,
    val fullLabel: String
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: String,
    val chips: List<String> = emptyList()
)

data class KpiMetrics(
    val total: String,
    val fuel: String,
    val lube: String,
    val volume: String,
    val totalChange: String,
    val fuelChange: String,
    val lubeChange: String,
    val volChange: String
)

data class DepotTank(
    val id: String,
    val name: String,
    val product: String,
    val fillPercent: Int,
    val capacityKL: Int,
    val status: String,
    val colorHex: Long
)

data class SapBillingItem(
    val billingDocument: String,
    val billingDocumentItem: String,
    val billingDocumentDate: String,
    val salesOrganization: String,
    val locationId: String,
    val plantName: String,
    val customerId: String,
    val customerName: String,
    val customerType: String,
    val productId: String,
    val productName: String,
    val productCategory: String,
    val salesUnit: String,
    val transactionCurrency: String,
    val quantity: Double,
    val netAmount: Double,
    val grossAmount: Double,
    val discountAmount: Double
)

data class SapConnectionInfo(
    val baseUrl: String = "https://my430716-api.s4hana.cloud.sap",
    val servicePath: String = "/sap/opu/odata4/sap/zani_ui_gal_bind/srvd_a2x/sap/zani_ui_gal_sales/0001/",
    val client: String = "080",
    val hasCredentials: Boolean = true,
    val lastStatus: String = "CONNECTED",
    val lastSyncTime: String = "2026-10-06T19:29:10Z",
    val detectedEntitySets: List<String> = listOf("BillingItem", "Customer", "Location", "Product", "SalesItem", "SalesKpi"),
    val lastErrorMessage: String = "",
    val serviceBinding: String = "ZANI_UI_GAL_BIND",
    val serviceDefinition: String = "ZANI_UI_GAL_SALES"
)

enum class ScreenRoute {
    SIGN_IN,
    HOME,
    LOCATION_MAP,
    MOMBASA_DEPOT,
    PRODUCT_PERFORMANCE,
    ALL_LOCATIONS,
    SALES_TREND,
    KEY_CUSTOMERS,
    GENERATE_REPORT,
    ASK_AI,
    SAP_LIVE_SYNC,
    DATA_UPLOAD
}

enum class Timeframe {
    TODAY,
    WEEK,
    MONTH
}

sealed class ActiveDataSource {
    object StaticDefault : ActiveDataSource()
    data class FileUploaded(
        val fileName: String,
        val recordCount: Int,
        val uploadTime: String,
        val fileSizeKb: Long = 0
    ) : ActiveDataSource()
    data class LiveSapApi(
        val recordCount: Int,
        val syncTime: String,
        val endpoint: String
    ) : ActiveDataSource()
}
