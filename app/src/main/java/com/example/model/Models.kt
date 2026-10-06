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
    ASK_AI
}

enum class Timeframe {
    TODAY,
    WEEK,
    MONTH
}
