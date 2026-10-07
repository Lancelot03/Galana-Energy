package com.example.data

import com.example.model.*
import java.util.Locale

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

    fun computeKpis(timeframe: Timeframe, items: List<SapBillingItem>): KpiMetrics {
        if (items.isEmpty()) return getKpis(timeframe)

        val totalNetRaw = items.sumOf { it.netAmount }
        val fuelNetRaw = items.filter { it.productCategory.equals("Fuel", ignoreCase = true) }.sumOf { it.netAmount }
        val lubeNetRaw = items.filter { it.productCategory.equals("Lubricants", ignoreCase = true) }.sumOf { it.netAmount }
        val totalVolumeRaw = items.sumOf { it.quantity }

        val factor = when (timeframe) {
            Timeframe.TODAY -> 1.0
            Timeframe.WEEK -> 5.5
            Timeframe.MONTH -> 22.0
        }

        val totalNetM = (totalNetRaw * factor) / 1_000_000.0
        val fuelNetM = (fuelNetRaw * factor) / 1_000_000.0
        val lubeNetM = (lubeNetRaw * factor) / 1_000_000.0
        val volKl = totalVolumeRaw * factor

        val totalFormatted = String.format(Locale.US, "%.1fM", totalNetM)
        val fuelFormatted = String.format(Locale.US, "%.1fM", fuelNetM)
        val lubeFormatted = String.format(Locale.US, "%.1fM", lubeNetM)
        val volFormatted = if (volKl >= 1000) String.format(Locale.US, "%,.0f", volKl) else String.format(Locale.US, "%.0f", volKl)

        val totalChange = when (timeframe) {
            Timeframe.TODAY -> "8.2%"
            Timeframe.WEEK -> "11.4%"
            Timeframe.MONTH -> "14.1%"
        }
        val fuelChange = when (timeframe) {
            Timeframe.TODAY -> "9.1%"
            Timeframe.WEEK -> "12.2%"
            Timeframe.MONTH -> "15.3%"
        }
        val lubeChange = when (timeframe) {
            Timeframe.TODAY -> "3.4%"
            Timeframe.WEEK -> "5.8%"
            Timeframe.MONTH -> "7.2%"
        }
        val volChange = when (timeframe) {
            Timeframe.TODAY -> "6.7%"
            Timeframe.WEEK -> "8.9%"
            Timeframe.MONTH -> "11.5%"
        }

        return KpiMetrics(
            total = totalFormatted,
            fuel = fuelFormatted,
            lube = lubeFormatted,
            volume = volFormatted,
            totalChange = totalChange,
            fuelChange = fuelChange,
            lubeChange = lubeChange,
            volChange = volChange
        )
    }

    fun computeLocations(items: List<SapBillingItem>): List<LocationItem> {
        if (items.isEmpty()) return locations

        val plantMap = items.groupBy { it.plantName.trim() }

        val updatedList = locations.map { loc ->
            val matchingItems = plantMap.entries.firstOrNull {
                it.key.contains(loc.name, ignoreCase = true) || loc.name.contains(it.key, ignoreCase = true)
            }?.value

            if (matchingItems != null && matchingItems.isNotEmpty()) {
                val netM = matchingItems.sumOf { it.netAmount } / 1_000_000.0
                val vol = matchingItems.sumOf { it.quantity }.toInt()
                loc.copy(
                    salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: loc.salesKES,
                    volumeKL = vol,
                    status = if (loc.type == "Depot") "Operational • Real-time Active" else "Operational"
                )
            } else {
                loc
            }
        }.toMutableList()

        plantMap.forEach { (plantName, plantItems) ->
            val exists = updatedList.any { it.name.contains(plantName, ignoreCase = true) || plantName.contains(it.name, ignoreCase = true) }
            if (!exists) {
                val netM = plantItems.sumOf { it.netAmount } / 1_000_000.0
                val vol = plantItems.sumOf { it.quantity }.toInt()
                val isDepot = plantName.contains("depot", ignoreCase = true) || plantName.contains("terminal", ignoreCase = true)
                updatedList.add(
                    LocationItem(
                        id = "custom-${plantName.lowercase(Locale.ROOT).replace(" ", "-")}",
                        name = plantName,
                        type = if (isDepot) "Depot" else "Station",
                        region = "Inland",
                        salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: 1.0,
                        change = 5.0,
                        isPositive = true,
                        coordsX = 45f,
                        coordsY = 50f,
                        volumeKL = vol,
                        status = "Active Ingested Plant"
                    )
                )
            }
        }

        return updatedList
    }

    fun computeProducts(items: List<SapBillingItem>): List<ProductItem> {
        if (items.isEmpty()) return products

        val productMap = items.groupBy { it.productName.trim() }

        val updatedList = products.map { prod ->
            val matchingItems = productMap.entries.firstOrNull {
                it.key.contains(prod.name, ignoreCase = true) || prod.name.contains(it.key, ignoreCase = true) ||
                        it.value.firstOrNull()?.productId.equals(prod.id, ignoreCase = true)
            }?.value

            if (matchingItems != null && matchingItems.isNotEmpty()) {
                val netM = matchingItems.sumOf { it.netAmount } / 1_000_000.0
                val vol = matchingItems.sumOf { it.quantity }.toInt()
                prod.copy(
                    salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: prod.salesKES,
                    volumeKL = vol
                )
            } else {
                prod
            }
        }.toMutableList()

        productMap.forEach { (prodName, prodItems) ->
            val exists = updatedList.any { it.name.contains(prodName, ignoreCase = true) || prodName.contains(it.name, ignoreCase = true) }
            if (!exists) {
                val first = prodItems.first()
                val netM = prodItems.sumOf { it.netAmount } / 1_000_000.0
                val vol = prodItems.sumOf { it.quantity }.toInt()
                updatedList.add(
                    ProductItem(
                        id = first.productId.lowercase(Locale.ROOT),
                        name = prodName,
                        category = first.productCategory,
                        volumeKL = vol,
                        salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: 1.0,
                        change = 8.0,
                        isPositive = true,
                        colorHex = if (first.productCategory.equals("Fuel", ignoreCase = true)) 0xFF0284C7 else 0xFF8B5CF6,
                        iconType = if (first.productCategory.equals("Fuel", ignoreCase = true)) "fuel" else "oil",
                        unitPriceKES = if (vol > 0) (prodItems.sumOf { it.netAmount } / vol) else 180.0,
                        grossMarginPercent = 15.0,
                        description = "Ingested product from SAP ledger / uploaded file."
                    )
                )
            }
        }

        return updatedList
    }

    fun computeCustomers(items: List<SapBillingItem>): List<CustomerItem> {
        if (items.isEmpty()) return customers

        val custMap = items.groupBy { it.customerName.trim() }

        val updatedList = customers.map { cust ->
            val matchingItems = custMap.entries.firstOrNull {
                it.key.contains(cust.name, ignoreCase = true) || cust.name.contains(it.key, ignoreCase = true) ||
                        it.value.firstOrNull()?.customerId.equals(cust.id, ignoreCase = true) ||
                        it.value.firstOrNull()?.customerId.equals(cust.code, ignoreCase = true)
            }?.value

            if (matchingItems != null && matchingItems.isNotEmpty()) {
                val netM = matchingItems.sumOf { it.netAmount } / 1_000_000.0
                cust.copy(
                    salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: cust.salesKES
                )
            } else {
                cust
            }
        }.toMutableList()

        custMap.forEach { (custName, custItems) ->
            val exists = updatedList.any { it.name.contains(custName, ignoreCase = true) || custName.contains(it.name, ignoreCase = true) }
            if (!exists) {
                val first = custItems.first()
                val netM = custItems.sumOf { it.netAmount } / 1_000_000.0
                updatedList.add(
                    CustomerItem(
                        id = first.customerId.lowercase(Locale.ROOT),
                        name = custName,
                        category = first.customerType,
                        salesKES = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: 0.5,
                        change = 6.0,
                        isPositive = true,
                        code = first.customerId,
                        colorHex = 0xFF0284C7,
                        contractsActive = 1,
                        creditLimitKES = "KES 20M",
                        paymentPerformance = "100% On-Time"
                    )
                )
            }
        }

        return updatedList.sortedByDescending { it.salesKES }
    }

    fun computeDailyTrends(items: List<SapBillingItem>): List<DailyTrend> {
        if (items.isEmpty()) return dailyTrends

        val byDate = items.groupBy { it.billingDocumentDate }.toSortedMap()
        if (byDate.size < 2) {
            val totalM = items.sumOf { it.netAmount } / 1_000_000.0
            val totalVol = items.sumOf { it.quantity }.toInt()
            val lastDay = byDate.keys.firstOrNull() ?: "Today"
            return dailyTrends.dropLast(1) + DailyTrend(
                day = lastDay.takeLast(5),
                value = String.format(Locale.US, "%.2f", totalM).toDoubleOrNull() ?: 2.4,
                volumeKL = totalVol,
                marginPercent = 15.5,
                fullLabel = "KES ${String.format(Locale.US, "%.2f", totalM)}M • $totalVol KL"
            )
        }

        return byDate.map { (date, docItems) ->
            val netM = docItems.sumOf { it.netAmount } / 1_000_000.0
            val vol = docItems.sumOf { it.quantity }.toInt()
            val dayLabel = date.takeLast(5)
            DailyTrend(
                day = dayLabel,
                value = String.format(Locale.US, "%.2f", netM).toDoubleOrNull() ?: 1.0,
                volumeKL = vol,
                marginPercent = 15.0,
                fullLabel = "KES ${String.format(Locale.US, "%.2f", netM)}M • $vol KL"
            )
        }
    }

    fun computeRegionalSales(items: List<SapBillingItem>): List<RegionalSales> {
        if (items.isEmpty()) return regionalSales

        val coastNet = items.filter { it.plantName.contains("Mombasa", ignoreCase = true) || it.plantName.contains("Malindi", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0
        val nairobiNet = items.filter { it.plantName.contains("Nairobi", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0
        val westernNet = items.filter { it.plantName.contains("Kisumu", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0
        val riftNet = items.filter { it.plantName.contains("Eldoret", ignoreCase = true) || it.plantName.contains("Nakuru", ignoreCase = true) }.sumOf { it.netAmount } / 1_000_000.0

        return listOf(
            RegionalSales("Coast", String.format(Locale.US, "%.1f", if (coastNet > 0) coastNet else 5.8).toDoubleOrNull() ?: 5.8, 11.2, true, 6),
            RegionalSales("Nairobi", String.format(Locale.US, "%.1f", if (nairobiNet > 0) nairobiNet else 4.6).toDoubleOrNull() ?: 4.6, 7.5, true, 8),
            RegionalSales("Western", String.format(Locale.US, "%.1f", if (westernNet > 0) westernNet else 1.8).toDoubleOrNull() ?: 1.8, 6.3, true, 5),
            RegionalSales("Rift Valley", String.format(Locale.US, "%.1f", if (riftNet > 0) riftNet else 1.6).toDoubleOrNull() ?: 1.6, 3.9, true, 5),
            RegionalSales("Eastern", 1.1, 2.4, false, 4)
        )
    }

    fun computeMombasaTanks(items: List<SapBillingItem>): List<DepotTank> {
        val mombasaItems = items.filter { it.plantName.contains("Mombasa", ignoreCase = true) }
        val dieselVol = mombasaItems.filter { it.productCategory.equals("Fuel", ignoreCase = true) && it.productId.contains("DIESEL", ignoreCase = true) }.sumOf { it.quantity }.toInt()
        val pmsVol = mombasaItems.filter { it.productId.contains("PMS", ignoreCase = true) || it.productId.contains("SUPER", ignoreCase = true) }.sumOf { it.quantity }.toInt()
        val jetVol = mombasaItems.filter { it.productId.contains("JET", ignoreCase = true) || it.productId.contains("ATF", ignoreCase = true) }.sumOf { it.quantity }.toInt()

        return listOf(
            DepotTank("T-01", "Tank 1", "Diesel (AGO)", if (dieselVol > 0) 95 else 90, 15000, if (dieselVol > 0) "Active Dispatches (${dieselVol} KL)" else "Operational", 0xFF0284C7),
            DepotTank("T-02", "Tank 2", "Super Petrol (PMS)", if (pmsVol > 0) 88 else 85, 12000, if (pmsVol > 0) "Active Dispatches (${pmsVol} KL)" else "Operational", 0xFF10B981),
            DepotTank("T-03", "Tank 3", "Jet A-1 (ATF)", if (jetVol > 0) 75 else 65, 8000, "Receiving Berth 1", 0xFFEF4444),
            DepotTank("T-04", "Tank 4", "Kerosene (IK)", 78, 6000, "Operational", 0xFFF59E0B)
        )
    }
}
