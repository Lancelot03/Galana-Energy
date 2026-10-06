package com.example.data

import com.example.model.*

object EnergyRepository {

    val kpiToday = KpiMetrics(
        total = "12.4M",
        fuel = "10.6M",
        lube = "1.8M",
        volume = "412",
        totalChange = "8.2%",
        fuelChange = "9.1%",
        lubeChange = "3.4%",
        volChange = "6.7%"
    )

    val kpiWeek = KpiMetrics(
        total = "78.2M",
        fuel = "67.4M",
        lube = "10.8M",
        volume = "2,640",
        totalChange = "11.4%",
        fuelChange = "12.2%",
        lubeChange = "5.8%",
        volChange = "8.9%"
    )

    val kpiMonth = KpiMetrics(
        total = "312.5M",
        fuel = "268.0M",
        lube = "44.5M",
        volume = "10,850",
        totalChange = "14.1%",
        fuelChange = "15.3%",
        lubeChange = "7.2%",
        volChange = "11.5%"
    )

    fun getKpis(timeframe: Timeframe): KpiMetrics = when (timeframe) {
        Timeframe.TODAY -> kpiToday
        Timeframe.WEEK -> kpiWeek
        Timeframe.MONTH -> kpiMonth
    }

    val locations: List<LocationItem> = listOf(
        LocationItem(
            id = "mombasa-depot",
            name = "Mombasa Depot",
            type = "Depot",
            region = "Coast",
            salesKES = 2.84,
            change = 12.6,
            isPositive = true,
            coordsX = 74f,
            coordsY = 84f,
            volumeKL = 78,
            highlight = true,
            status = "Operational • Berth 1 Link"
        ),
        LocationItem(
            id = "nairobi-west",
            name = "Nairobi West",
            type = "Station",
            region = "Nairobi",
            salesKES = 1.96,
            change = 8.4,
            isPositive = true,
            coordsX = 50f,
            coordsY = 64f,
            volumeKL = 54,
            status = "Operational"
        ),
        LocationItem(
            id = "nairobi-east",
            name = "Nairobi East",
            type = "Station",
            region = "Nairobi",
            salesKES = 1.32,
            change = 5.1,
            isPositive = true,
            coordsX = 53f,
            coordsY = 63f,
            volumeKL = 38,
            status = "Operational"
        ),
        LocationItem(
            id = "kisumu",
            name = "Kisumu",
            type = "Depot",
            region = "Western",
            salesKES = 1.12,
            change = 7.3,
            isPositive = true,
            coordsX = 26f,
            coordsY = 55f,
            volumeKL = 32,
            status = "Operational • Lake Hub"
        ),
        LocationItem(
            id = "nakuru",
            name = "Nakuru",
            type = "Station",
            region = "Rift Valley",
            salesKES = 0.94,
            change = 6.8,
            isPositive = true,
            coordsX = 38f,
            coordsY = 54f,
            volumeKL = 27,
            status = "Operational"
        ),
        LocationItem(
            id = "eldoret",
            name = "Eldoret",
            type = "Depot",
            region = "Rift Valley",
            salesKES = 0.86,
            change = 3.2,
            isPositive = true,
            coordsX = 28f,
            coordsY = 44f,
            volumeKL = 24,
            status = "Operational • Pipeline Offtake"
        ),
        LocationItem(
            id = "malindi",
            name = "Malindi",
            type = "Station",
            region = "Coast",
            salesKES = 0.72,
            change = 11.1,
            isPositive = true,
            coordsX = 80f,
            coordsY = 77f,
            volumeKL = 21,
            status = "Operational"
        ),
        LocationItem(
            id = "garissa",
            name = "Garissa",
            type = "Station",
            region = "Eastern",
            salesKES = 0.54,
            change = 1.9,
            isPositive = false,
            coordsX = 68f,
            coordsY = 52f,
            volumeKL = 16,
            status = "Operational"
        ),
        LocationItem(
            id = "marsabit",
            name = "Marsabit",
            type = "Station",
            region = "Eastern",
            salesKES = 0.28,
            change = 4.5,
            isPositive = true,
            coordsX = 54f,
            coordsY = 26f,
            volumeKL = 8,
            status = "Operational"
        )
    )

    val products: List<ProductItem> = listOf(
        ProductItem(
            id = "diesel",
            name = "Diesel (AGO)",
            category = "Fuel",
            volumeKL = 1260,
            salesKES = 3.78,
            change = 10.8,
            isPositive = true,
            colorHex = 0xFF0284C7,
            iconType = "fuel",
            unitPriceKES = 180.50,
            grossMarginPercent = 14.2,
            description = "Automotive Gasoil standard 50ppm sulfur for heavy commercial and industrial power."
        ),
        ProductItem(
            id = "petrol",
            name = "Super Petrol (PMS)",
            category = "Fuel",
            volumeKL = 980,
            salesKES = 3.44,
            change = 7.6,
            isPositive = true,
            colorHex = 0xFF10B981,
            iconType = "fuel",
            unitPriceKES = 195.20,
            grossMarginPercent = 12.8,
            description = "Premium Motor Spirit with advanced detergents for retail fleets and light transport."
        ),
        ProductItem(
            id = "kerosene",
            name = "Kerosene (IK)",
            category = "Fuel",
            volumeKL = 140,
            salesKES = 0.36,
            change = 5.1,
            isPositive = true,
            colorHex = 0xFFF59E0B,
            iconType = "canister",
            unitPriceKES = 168.00,
            grossMarginPercent = 11.5,
            description = "Illuminating Kerosene for domestic heating and industrial cleaning applications."
        ),
        ProductItem(
            id = "atf",
            name = "Aviation Turbine Fuel (Jet A-1)",
            category = "Fuel",
            volumeKL = 45,
            salesKES = 0.54,
            change = 12.3,
            isPositive = true,
            colorHex = 0xFFEF4444,
            iconType = "plane",
            unitPriceKES = 210.00,
            grossMarginPercent = 18.0,
            description = "Aviation Turbine Fuel certified to AFQRJOS specs for commercial carriers at JKIA & MIA."
        ),
        ProductItem(
            id = "engine-oil-15w40",
            name = "Engine Oil 15W40",
            category = "Lubricants",
            volumeKL = 32,
            salesKES = 0.42,
            change = 6.8,
            isPositive = true,
            colorHex = 0xFF8B5CF6,
            iconType = "oil",
            unitPriceKES = 420.00,
            grossMarginPercent = 28.5,
            description = "Heavy duty fleet multigrade oil API CI-4/SL for turbocharged diesel transport."
        ),
        ProductItem(
            id = "gear-oil",
            name = "Gear Oil EP90 / 85W140",
            category = "Lubricants",
            volumeKL = 28,
            salesKES = 0.38,
            change = 4.2,
            isPositive = true,
            colorHex = 0xFFF97316,
            iconType = "gear",
            unitPriceKES = 460.00,
            grossMarginPercent = 26.0,
            description = "Extreme pressure automotive gear lubricant API GL-5 for severe duty differentials."
        ),
        ProductItem(
            id = "grease",
            name = "Industrial Lithium Grease",
            category = "Lubricants",
            volumeKL = 18,
            salesKES = 0.21,
            change = 2.1,
            isPositive = false,
            colorHex = 0xFF64748B,
            iconType = "grease",
            unitPriceKES = 510.00,
            grossMarginPercent = 24.2,
            description = "Multi-purpose NLGI 2 lithium complex grease with high thermal and water stability."
        )
    )

    val customers: List<CustomerItem> = listOf(
        CustomerItem(
            id = "kengen",
            name = "KenGen Power",
            category = "Industrial",
            salesKES = 2.14,
            change = 12.1,
            isPositive = true,
            code = "KG",
            colorHex = 0xFF0284C7,
            contractsActive = 3,
            creditLimitKES = "KES 35M",
            paymentPerformance = "100% On-Time (15 days)"
        ),
        CustomerItem(
            id = "kpa",
            name = "Kenya Ports Authority",
            category = "Industrial",
            salesKES = 1.86,
            change = 8.4,
            isPositive = true,
            code = "KPA",
            colorHex = 0xFF0369A1,
            contractsActive = 2,
            creditLimitKES = "KES 25M",
            paymentPerformance = "100% On-Time (30 days)"
        ),
        CustomerItem(
            id = "kenya-airways",
            name = "Kenya Airways (KQ)",
            category = "Fleet",
            salesKES = 1.22,
            change = 6.8,
            isPositive = true,
            code = "KQ",
            colorHex = 0xFFDC2626,
            contractsActive = 2,
            creditLimitKES = "KES 40M",
            paymentPerformance = "98% On-Time"
        ),
        CustomerItem(
            id = "bamburi-cement",
            name = "Bamburi Cement Ltd",
            category = "Industrial",
            salesKES = 0.98,
            change = 5.6,
            isPositive = true,
            code = "BC",
            colorHex = 0xFF15803D,
            contractsActive = 1,
            creditLimitKES = "KES 15M",
            paymentPerformance = "100% On-Time"
        ),
        CustomerItem(
            id = "tatu-city",
            name = "Tatu City Infrastructure",
            category = "Industrial",
            salesKES = 0.76,
            change = 10.2,
            isPositive = true,
            code = "TC",
            colorHex = 0xFFD97706,
            contractsActive = 2,
            creditLimitKES = "KES 10M",
            paymentPerformance = "100% On-Time"
        ),
        CustomerItem(
            id = "ea-portland",
            name = "East Africa Portland Cement",
            category = "Wholesalers",
            salesKES = 0.62,
            change = 4.1,
            isPositive = true,
            code = "EAP",
            colorHex = 0xFF2563EB,
            contractsActive = 1,
            creditLimitKES = "KES 8M",
            paymentPerformance = "95% On-Time"
        ),
        CustomerItem(
            id = "sameer-africa",
            name = "Sameer Africa",
            category = "Wholesalers",
            salesKES = 0.54,
            change = 1.8,
            isPositive = false,
            code = "SA",
            colorHex = 0xFF475569,
            contractsActive = 1,
            creditLimitKES = "KES 6M",
            paymentPerformance = "92% On-Time"
        ),
        CustomerItem(
            id = "county-mombasa",
            name = "County Govt. of Mombasa",
            category = "Fleet",
            salesKES = 0.48,
            change = 6.9,
            isPositive = true,
            code = "CGM",
            colorHex = 0xFF059669,
            contractsActive = 1,
            creditLimitKES = "KES 12M",
            paymentPerformance = "100% On-Time"
        )
    )

    val regionalSales: List<RegionalSales> = listOf(
        RegionalSales("Coast", 5.8, 11.2, true, 6),
        RegionalSales("Nairobi", 4.6, 7.5, true, 8),
        RegionalSales("Western", 1.8, 6.3, true, 5),
        RegionalSales("Rift Valley", 1.6, 3.9, true, 5),
        RegionalSales("Eastern", 1.1, 2.4, false, 4)
    )

    val dailyTrends: List<DailyTrend> = listOf(
        DailyTrend("20 Sep", 0.8, 28, 14.1, "KES 0.8M • 28 KL"),
        DailyTrend("21 Sep", 1.1, 36, 14.5, "KES 1.1M • 36 KL"),
        DailyTrend("22 Sep", 1.3, 42, 15.0, "KES 1.3M • 42 KL"),
        DailyTrend("23 Sep", 1.5, 49, 14.8, "KES 1.5M • 49 KL"),
        DailyTrend("24 Sep", 1.8, 58, 15.2, "KES 1.8M • 58 KL"),
        DailyTrend("25 Sep", 2.1, 68, 15.6, "KES 2.1M • 68 KL"),
        DailyTrend("26 Sep", 2.4, 76, 16.1, "KES 2.4M • 76 KL")
    )

    val mombasaTanks: List<DepotTank> = listOf(
        DepotTank("T-01", "Tank 1", "Diesel (AGO)", 95, 15000, "Full / Active Dispatch", 0xFF0284C7),
        DepotTank("T-02", "Tank 2", "Super Petrol (PMS)", 88, 12000, "Operational", 0xFF10B981),
        DepotTank("T-03", "Tank 3", "Jet A-1 (ATF)", 65, 8000, "Receiving Berth 1", 0xFFEF4444),
        DepotTank("T-04", "Tank 4", "Kerosene (IK)", 78, 6000, "Operational", 0xFFF59E0B)
    )

    val initialChatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "msg-1",
            sender = "user",
            text = "Show me why Mombasa sales increased in September.",
            timestamp = "10:14 AM"
        ),
        ChatMessage(
            id = "msg-2",
            sender = "ai",
            text = "Mombasa sales surged by 12.6% in September 2026 compared to August.\n\nKey Operational Drivers:\n1. Diesel volume expanded by 18% driven by high off-take at the Kilindini maritime terminal and KenGen Kipevu.\n2. Two new industrial fleet accounts contributed KES 0.42M in baseline contracts.\n3. Marine & heavy duty lubricant sales rose by 9.1% following increased port operations at Berth 1 & 2.",
            timestamp = "10:14 AM",
            chips = listOf("Show detailed data", "Top products", "View customers", "View by day")
        )
    )
}
