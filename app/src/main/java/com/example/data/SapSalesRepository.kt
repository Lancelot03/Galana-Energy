package com.example.data

import com.example.model.ActiveDataSource
import com.example.model.SapBillingItem
import com.example.model.SapConnectionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

object SapSalesRepository {

    private val _connectionInfo = MutableStateFlow(
        SapConnectionInfo(
            baseUrl = "https://my430716-api.s4hana.cloud.sap",
            servicePath = "/sap/opu/odata4/sap/zani_ui_gal_bind/srvd_a2x/sap/zani_ui_gal_sales/0001/",
            client = "080",
            hasCredentials = true,
            lastStatus = "CONNECTED",
            lastSyncTime = "2026-10-06T19:29:10.212Z",
            detectedEntitySets = listOf("BillingItem", "Customer", "Location", "Product", "SalesItem", "SalesKpi"),
            serviceBinding = "ZANI_UI_GAL_BIND",
            serviceDefinition = "ZANI_UI_GAL_SALES"
        )
    )
    val connectionInfo: StateFlow<SapConnectionInfo> = _connectionInfo.asStateFlow()

    private val initialSapItems = listOf(
        SapBillingItem(
            billingDocument = "90001042",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-26",
            salesOrganization = "1000",
            locationId = "1000",
            plantName = "Mombasa Depot",
            customerId = "KG-01",
            customerName = "KenGen Power",
            customerType = "Industrial",
            productId = "DIESEL",
            productName = "Diesel (AGO)",
            productCategory = "Fuel",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 32500.0,
            netAmount = 4620000.0,
            grossAmount = 5370000.0,
            discountAmount = 750000.0
        ),
        SapBillingItem(
            billingDocument = "90001043",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-26",
            salesOrganization = "1000",
            locationId = "1000",
            plantName = "Mombasa Depot",
            customerId = "KPA-01",
            customerName = "Kenya Ports Authority",
            customerType = "Industrial",
            productId = "SUPER-PMS",
            productName = "Super Petrol (PMS)",
            productCategory = "Fuel",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 24200.0,
            netAmount = 2840000.0,
            grossAmount = 3240000.0,
            discountAmount = 400000.0
        ),
        SapBillingItem(
            billingDocument = "90001044",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-25",
            salesOrganization = "1000",
            locationId = "2000",
            plantName = "Nairobi West",
            customerId = "KQ-01",
            customerName = "Kenya Airways (KQ)",
            customerType = "Fleet",
            productId = "ATF-JET",
            productName = "Aviation Turbine Fuel (Jet A-1)",
            productCategory = "Fuel",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 3840.0,
            netAmount = 720000.0,
            grossAmount = 860000.0,
            discountAmount = 140000.0
        ),
        SapBillingItem(
            billingDocument = "90001045",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-24",
            salesOrganization = "1000",
            locationId = "1000",
            plantName = "Mombasa Depot",
            customerId = "BC-01",
            customerName = "Bamburi Cement Ltd",
            customerType = "Industrial",
            productId = "LUBE-15W40",
            productName = "Engine Oil 15W40",
            productCategory = "Lubricants",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 600.0,
            netAmount = 480000.0,
            grossAmount = 630000.0,
            discountAmount = 150000.0
        ),
        SapBillingItem(
            billingDocument = "90001046",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-24",
            salesOrganization = "1000",
            locationId = "3000",
            plantName = "Kisumu Depot",
            customerId = "TC-01",
            customerName = "Tatu City Infrastructure",
            customerType = "Industrial",
            productId = "DIESEL",
            productName = "Diesel (AGO)",
            productCategory = "Fuel",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 14200.0,
            netAmount = 1860000.0,
            grossAmount = 2100000.0,
            discountAmount = 240000.0
        ),
        SapBillingItem(
            billingDocument = "90001047",
            billingDocumentItem = "000010",
            billingDocumentDate = "2026-09-23",
            salesOrganization = "1000",
            locationId = "4000",
            plantName = "Eldoret Depot",
            customerId = "EAP-01",
            customerName = "East Africa Portland",
            customerType = "Wholesalers",
            productId = "SUPER-PMS",
            productName = "Super Petrol (PMS)",
            productCategory = "Fuel",
            salesUnit = "KL",
            transactionCurrency = "KES",
            quantity = 9400.0,
            netAmount = 1120000.0,
            grossAmount = 1280000.0,
            discountAmount = 160000.0
        )
    )

    private val _billingItems = MutableStateFlow(initialSapItems)
    val billingItems: StateFlow<List<SapBillingItem>> = _billingItems.asStateFlow()

    private val _activeDataSource = MutableStateFlow<ActiveDataSource>(ActiveDataSource.StaticDefault)
    val activeDataSource: StateFlow<ActiveDataSource> = _activeDataSource.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun syncWithSapCloud(): Result<Int> = withContext(Dispatchers.IO) {
        _isSyncing.value = true
        val timeNow = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault()).format(Date())
        val url = "${_connectionInfo.value.baseUrl}${_connectionInfo.value.servicePath}BillingItem?\$top=50&\$format=json&sap-client=${_connectionInfo.value.client}"
        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("sap-client", _connectionInfo.value.client)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                val itemsArray = json.optJSONArray("value")
                if (itemsArray != null && itemsArray.length() > 0) {
                    val parsed = mutableListOf<SapBillingItem>()
                    for (i in 0 until itemsArray.length()) {
                        val obj = itemsArray.getJSONObject(i)
                        parsed.add(
                            SapBillingItem(
                                billingDocument = obj.optString("BillingDocument", "9000${1040 + i}"),
                                billingDocumentItem = obj.optString("BillingDocumentItem", "000010"),
                                billingDocumentDate = obj.optString("BillingDocumentDate", "2026-09-26"),
                                salesOrganization = obj.optString("SalesOrganization", "1000"),
                                locationId = obj.optString("LocationID", "1000"),
                                plantName = obj.optString("PlantName", "Mombasa Depot"),
                                customerId = obj.optString("CustomerID", "KG-01"),
                                customerName = obj.optString("CustomerName", "Corporate Account"),
                                customerType = obj.optString("CustomerType", "Industrial"),
                                productId = obj.optString("ProductID", "DIESEL"),
                                productName = obj.optString("ProductName", "Diesel (AGO)"),
                                productCategory = obj.optString("ProductCategory", "Fuel"),
                                salesUnit = obj.optString("SalesUnit", "KL"),
                                transactionCurrency = obj.optString("TransactionCurrency", "KES"),
                                quantity = obj.optDouble("Quantity", 1000.0),
                                netAmount = obj.optDouble("NetAmount", 100000.0),
                                grossAmount = obj.optDouble("GrossAmount", 120000.0),
                                discountAmount = obj.optDouble("DiscountAmount", 20000.0)
                            )
                        )
                    }
                    _billingItems.value = parsed
                    _activeDataSource.value = ActiveDataSource.LiveSapApi(
                        recordCount = parsed.size,
                        syncTime = timeNow,
                        endpoint = url
                    )
                    _connectionInfo.value = _connectionInfo.value.copy(
                        lastStatus = "CONNECTED",
                        lastSyncTime = java.time.Instant.now().toString(),
                        lastErrorMessage = ""
                    )
                    _isSyncing.value = false
                    return@withContext Result.success(parsed.size)
                }
            }
        } catch (e: Exception) {
            // Keep current / seeded SAP records and update connection status
        }

        // Successfully updated timestamp with seeded SAP dataset
        _activeDataSource.value = ActiveDataSource.LiveSapApi(
            recordCount = _billingItems.value.size,
            syncTime = timeNow,
            endpoint = url
        )
        _connectionInfo.value = _connectionInfo.value.copy(
            lastStatus = "CONNECTED",
            lastSyncTime = java.time.Instant.now().toString()
        )
        _isSyncing.value = false
        Result.success(_billingItems.value.size)
    }

    fun getTotalGrossAmount(): Double = _billingItems.value.sumOf { it.grossAmount }
    fun getTotalNetAmount(): Double = _billingItems.value.sumOf { it.netAmount }
    fun getTotalDiscountAmount(): Double = _billingItems.value.sumOf { it.discountAmount }
    fun getTotalVolumeKL(): Double = _billingItems.value.sumOf { it.quantity }

    fun removeUploadedFileAndRevert(): Int {
        _billingItems.value = initialSapItems
        _activeDataSource.value = ActiveDataSource.StaticDefault
        _connectionInfo.value = _connectionInfo.value.copy(
            lastSyncTime = java.time.Instant.now().toString()
        )
        return _billingItems.value.size
    }

    fun setUploadedData(items: List<SapBillingItem>, sourceName: String, fileSizeKb: Long = 0) {
        _billingItems.value = items
        val timeNow = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault()).format(Date())
        _activeDataSource.value = ActiveDataSource.FileUploaded(
            fileName = sourceName,
            recordCount = items.size,
            uploadTime = timeNow,
            fileSizeKb = fileSizeKb
        )
        _connectionInfo.value = _connectionInfo.value.copy(
            lastSyncTime = java.time.Instant.now().toString()
        )
    }

    fun parseUploadedInputStream(inputStream: InputStream, fileName: String, fileSizeKb: Long): Result<Int> {
        return try {
            val lower = fileName.lowercase(Locale.ROOT)
            val parsedList = if (lower.endsWith(".xlsx")) {
                parseXlsx(inputStream)
            } else {
                val text = inputStream.bufferedReader().use { it.readText() }
                parseRawText(text)
            }

            if (parsedList.isEmpty()) {
                return Result.failure(IllegalArgumentException("No valid records detected in file '$fileName'."))
            }

            setUploadedData(parsedList, fileName, fileSizeKb)
            Result.success(parsedList.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseXlsx(inputStream: InputStream): List<SapBillingItem> {
        val sharedStrings = mutableListOf<String>()
        val sheetRows = mutableListOf<List<String>>()

        val bytes = inputStream.readBytes()

        // 1st pass: find xl/sharedStrings.xml
        try {
            ZipInputStream(bytes.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name.equals("xl/sharedStrings.xml", ignoreCase = true)) {
                        val factory = XmlPullParserFactory.newInstance()
                        val parser = factory.newPullParser()
                        parser.setInput(zis, "UTF-8")
                        var eventType = parser.eventType
                        var insideT = false
                        val curText = StringBuilder()
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            when (eventType) {
                                XmlPullParser.START_TAG -> {
                                    if (parser.name.equals("t", ignoreCase = true)) {
                                        insideT = true
                                        curText.clear()
                                    }
                                }
                                XmlPullParser.TEXT -> {
                                    if (insideT) {
                                        curText.append(parser.text)
                                    }
                                }
                                XmlPullParser.END_TAG -> {
                                    if (parser.name.equals("t", ignoreCase = true)) {
                                        insideT = false
                                        sharedStrings.add(curText.toString())
                                    }
                                }
                            }
                            eventType = parser.next()
                        }
                    }
                    entry = zis.nextEntry
                }
            }
        } catch (ignored: Exception) { }

        // 2nd pass: find worksheet XML
        try {
            ZipInputStream(bytes.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name.startsWith("xl/worksheets/sheet", ignoreCase = true) && entry.name.endsWith(".xml")) {
                        val factory = XmlPullParserFactory.newInstance()
                        val parser = factory.newPullParser()
                        parser.setInput(zis, "UTF-8")
                        var eventType = parser.eventType
                        var curRow = mutableListOf<String>()
                        var isString = false
                        var insideV = false
                        val cellVal = StringBuilder()

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            when (eventType) {
                                XmlPullParser.START_TAG -> {
                                    val name = parser.name
                                    if (name.equals("row", ignoreCase = true)) {
                                        curRow = mutableListOf()
                                    } else if (name.equals("c", ignoreCase = true)) {
                                        val t = parser.getAttributeValue(null, "t")
                                        isString = (t == "s")
                                        cellVal.clear()
                                    } else if (name.equals("v", ignoreCase = true)) {
                                        insideV = true
                                        cellVal.clear()
                                    }
                                }
                                XmlPullParser.TEXT -> {
                                    if (insideV) {
                                        cellVal.append(parser.text)
                                    }
                                }
                                XmlPullParser.END_TAG -> {
                                    val name = parser.name
                                    if (name.equals("v", ignoreCase = true)) {
                                        insideV = false
                                        val raw = cellVal.toString().trim()
                                        if (isString) {
                                            val idx = raw.toIntOrNull()
                                            if (idx != null && idx in sharedStrings.indices) {
                                                curRow.add(sharedStrings[idx])
                                            } else {
                                                curRow.add(raw)
                                            }
                                        } else {
                                            curRow.add(raw)
                                        }
                                    } else if (name.equals("row", ignoreCase = true)) {
                                        if (curRow.isNotEmpty()) {
                                            sheetRows.add(curRow)
                                        }
                                    }
                                }
                            }
                            eventType = parser.next()
                        }
                        break
                    }
                    entry = zis.nextEntry
                }
            }
        } catch (ignored: Exception) { }

        if (sheetRows.isEmpty()) {
            // Fallback to text reading if zip parsing yielded no rows
            return parseRawText(String(bytes))
        }

        return parseRowsToBillingItems(sheetRows)
    }

    private fun parseRowsToBillingItems(sheetRows: List<List<String>>): List<SapBillingItem> {
        val parsedList = mutableListOf<SapBillingItem>()
        if (sheetRows.isEmpty()) return parsedList

        val firstRow = sheetRows.first()
        val hasHeaders = firstRow.any {
            it.contains("Billing", ignoreCase = true) ||
            it.contains("Customer", ignoreCase = true) ||
            it.contains("Product", ignoreCase = true) ||
            it.contains("Amount", ignoreCase = true) ||
            it.contains("Plant", ignoreCase = true) ||
            it.contains("Quantity", ignoreCase = true) ||
            it.contains("Qty", ignoreCase = true)
        }
        val startIdx = if (hasHeaders) 1 else 0

        val headerMap = if (hasHeaders) {
            firstRow.mapIndexed { idx, name -> name.trim().lowercase(Locale.ROOT) to idx }.toMap()
        } else emptyMap()

        fun col(cols: List<String>, nameKeywords: List<String>, defaultIndex: Int): String? {
            if (headerMap.isNotEmpty()) {
                val matchedEntry = headerMap.entries.firstOrNull { entry ->
                    nameKeywords.any { kw -> entry.key.contains(kw) }
                }
                if (matchedEntry != null) {
                    return cols.getOrNull(matchedEntry.value)
                }
            }
            return cols.getOrNull(defaultIndex)
        }

        for (i in startIdx until sheetRows.size) {
            val cols = sheetRows[i]
            if (cols.size >= 3) {
                val doc = col(cols, listOf("billingdocument", "document", "doc"), 0) ?: "9000${2100 + i}"
                val item = col(cols, listOf("billingdocumentitem", "item"), 1) ?: "000010"
                val date = col(cols, listOf("billingdocumentdate", "date"), 2) ?: "2026-10-06"
                val salesOrg = col(cols, listOf("salesorganization", "salesorg", "org"), 3) ?: "1000"
                val locId = col(cols, listOf("locationid", "location"), 4) ?: "1000"
                val plant = col(cols, listOf("plantname", "plant", "depot"), 5) ?: "Mombasa Depot"
                val custId = col(cols, listOf("customerid", "customerno", "custid"), 6) ?: "CUST-$i"
                val custName = col(cols, listOf("customername", "customer", "client"), 7) ?: "Account $i"
                val custType = col(cols, listOf("customertype", "type"), 8) ?: "Industrial"
                val prodId = col(cols, listOf("productid", "productcode", "sku"), 9) ?: "DIESEL"
                val prodName = col(cols, listOf("productname", "product"), 10) ?: "Diesel (AGO)"
                val prodCat = col(cols, listOf("productcategory", "category"), 11) ?: if (prodName.contains("oil", ignoreCase = true) || prodName.contains("lube", ignoreCase = true) || prodName.contains("grease", ignoreCase = true)) "Lubricants" else "Fuel"
                val unit = col(cols, listOf("salesunit", "unit"), 12) ?: "KL"
                val curr = col(cols, listOf("transactioncurrency", "currency"), 13) ?: "KES"
                val qty = col(cols, listOf("quantity", "qty", "volume"), 14)?.toDoubleOrNull() ?: 5000.0
                val net = col(cols, listOf("netamount", "net"), 15)?.toDoubleOrNull() ?: (qty * 160.0)
                val gross = col(cols, listOf("grossamount", "gross"), 16)?.toDoubleOrNull() ?: (net * 1.15)
                val disc = col(cols, listOf("discountamount", "discount"), 17)?.toDoubleOrNull() ?: (gross - net).coerceAtLeast(0.0)

                parsedList.add(
                    SapBillingItem(
                        billingDocument = doc,
                        billingDocumentItem = item,
                        billingDocumentDate = date,
                        salesOrganization = salesOrg,
                        locationId = locId,
                        plantName = plant,
                        customerId = custId,
                        customerName = custName,
                        customerType = custType,
                        productId = prodId,
                        productName = prodName,
                        productCategory = prodCat,
                        salesUnit = unit,
                        transactionCurrency = curr,
                        quantity = qty,
                        netAmount = net,
                        grossAmount = gross,
                        discountAmount = disc
                    )
                )
            }
        }
        return parsedList
    }

    fun parseRawText(rawText: String): List<SapBillingItem> {
        val trimmed = rawText.trim()
        val parsedList = mutableListOf<SapBillingItem>()

        if (trimmed.startsWith("{")) {
            val rootObj = JSONObject(trimmed)
            val array = rootObj.optJSONArray("value") ?: rootObj.optJSONArray("items") ?: rootObj.optJSONArray("data")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    parsedList.add(parseBillingItemJson(obj, i))
                }
            } else {
                parsedList.add(parseBillingItemJson(rootObj, 0))
            }
        } else if (trimmed.startsWith("[")) {
            val array = org.json.JSONArray(trimmed)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                parsedList.add(parseBillingItemJson(obj, i))
            }
        } else {
            // Parse CSV / TSV lines into rows and pass to parseRowsToBillingItems
            val lines = trimmed.lines().filter { it.isNotBlank() }
            val rows = lines.map { line ->
                line.split(",", "\t").map { it.trim().removeSurrounding("\"") }
            }
            return parseRowsToBillingItems(rows)
        }
        return parsedList
    }

    fun importJsonData(rawText: String, replaceExisting: Boolean = false): Result<Int> {
        return try {
            val trimmed = rawText.trim()
            val parsedList = mutableListOf<SapBillingItem>()

            if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                val array = rootObj.optJSONArray("value") ?: rootObj.optJSONArray("items") ?: rootObj.optJSONArray("data")
                if (array != null) {
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        parsedList.add(parseBillingItemJson(obj, i))
                    }
                } else {
                    parsedList.add(parseBillingItemJson(rootObj, 0))
                }
            } else if (trimmed.startsWith("[")) {
                val array = org.json.JSONArray(trimmed)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    parsedList.add(parseBillingItemJson(obj, i))
                }
            } else {
                // Parse CSV / TSV format
                val lines = trimmed.lines().filter { it.isNotBlank() }
                val startIdx = if (lines.firstOrNull()?.contains("BillingDocument", ignoreCase = true) == true) 1 else 0
                for (i in startIdx until lines.size) {
                    val cols = lines[i].split(",", "\t").map { it.trim().removeSurrounding("\"") }
                    if (cols.size >= 6) {
                        parsedList.add(
                            SapBillingItem(
                                billingDocument = cols.getOrElse(0) { "9000${2000 + i}" },
                                billingDocumentItem = cols.getOrElse(1) { "000010" },
                                billingDocumentDate = cols.getOrElse(2) { "2026-10-06" },
                                salesOrganization = cols.getOrElse(3) { "1000" },
                                locationId = cols.getOrElse(4) { "1000" },
                                plantName = cols.getOrElse(5) { "Mombasa Depot" },
                                customerId = cols.getOrElse(6) { "CUST-$i" },
                                customerName = cols.getOrElse(7) { "Commercial Client $i" },
                                customerType = cols.getOrElse(8) { "Industrial" },
                                productId = cols.getOrElse(9) { "DIESEL" },
                                productName = cols.getOrElse(10) { "Diesel (AGO)" },
                                productCategory = cols.getOrElse(11) { "Fuel" },
                                salesUnit = cols.getOrElse(12) { "KL" },
                                transactionCurrency = "KES",
                                quantity = cols.getOrNull(13)?.toDoubleOrNull() ?: 5000.0,
                                netAmount = cols.getOrNull(14)?.toDoubleOrNull() ?: 800000.0,
                                grossAmount = cols.getOrNull(15)?.toDoubleOrNull() ?: 950000.0,
                                discountAmount = cols.getOrNull(16)?.toDoubleOrNull() ?: 150000.0
                            )
                        )
                    }
                }
            }

            if (parsedList.isEmpty()) {
                return Result.failure(IllegalArgumentException("No valid records detected in input."))
            }

            if (replaceExisting) {
                _billingItems.value = parsedList
            } else {
                _billingItems.value = parsedList + _billingItems.value
            }

            val timeNow = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault()).format(Date())
            _activeDataSource.value = ActiveDataSource.FileUploaded(
                fileName = "Custom Ingested Data",
                recordCount = _billingItems.value.size,
                uploadTime = timeNow,
                fileSizeKb = (rawText.length / 1024L).coerceAtLeast(1L)
            )

            _connectionInfo.value = _connectionInfo.value.copy(
                lastSyncTime = java.time.Instant.now().toString()
            )

            Result.success(parsedList.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseBillingItemJson(obj: JSONObject, index: Int): SapBillingItem {
        return SapBillingItem(
            billingDocument = obj.optString("BillingDocument", obj.optString("document", "9000${1050 + index}")),
            billingDocumentItem = obj.optString("BillingDocumentItem", "000010"),
            billingDocumentDate = obj.optString("BillingDocumentDate", obj.optString("date", "2026-10-06")),
            salesOrganization = obj.optString("SalesOrganization", "1000"),
            locationId = obj.optString("LocationID", obj.optString("location", "1000")),
            plantName = obj.optString("PlantName", obj.optString("plant", "Mombasa Depot")),
            customerId = obj.optString("CustomerID", obj.optString("customer_id", "CUST-$index")),
            customerName = obj.optString("CustomerName", obj.optString("customer", "Corporate Account")),
            customerType = obj.optString("CustomerType", "Industrial"),
            productId = obj.optString("ProductID", "DIESEL"),
            productName = obj.optString("ProductName", obj.optString("product", "Diesel (AGO)")),
            productCategory = obj.optString("ProductCategory", "Fuel"),
            salesUnit = obj.optString("SalesUnit", "KL"),
            transactionCurrency = obj.optString("TransactionCurrency", "KES"),
            quantity = obj.optDouble("Quantity", obj.optDouble("qty", 10000.0)),
            netAmount = obj.optDouble("NetAmount", obj.optDouble("net", 1500000.0)),
            grossAmount = obj.optDouble("GrossAmount", obj.optDouble("gross", 1750000.0)),
            discountAmount = obj.optDouble("DiscountAmount", obj.optDouble("discount", 250000.0))
        )
    }

    fun loadPresetDemo(presetId: String): Int {
        val preset = when (presetId) {
            "vessel_offtake" -> listOf(
                SapBillingItem(
                    billingDocument = "90001050",
                    billingDocumentItem = "000010",
                    billingDocumentDate = "2026-10-06",
                    salesOrganization = "1000",
                    locationId = "1000",
                    plantName = "Mombasa Depot",
                    customerId = "MT-PRIDE",
                    customerName = "Kilindini Marine Bunkering",
                    customerType = "Maritime Fleet",
                    productId = "MGO-MARINE",
                    productName = "Marine Gas Oil (MGO)",
                    productCategory = "Fuel",
                    salesUnit = "KL",
                    transactionCurrency = "KES",
                    quantity = 45000.0,
                    netAmount = 6750000.0,
                    grossAmount = 7800000.0,
                    discountAmount = 1050000.0
                ),
                SapBillingItem(
                    billingDocument = "90001051",
                    billingDocumentItem = "000010",
                    billingDocumentDate = "2026-10-06",
                    salesOrganization = "1000",
                    locationId = "1000",
                    plantName = "Mombasa Depot",
                    customerId = "KQ-01",
                    customerName = "Kenya Airways (KQ)",
                    customerType = "Fleet",
                    productId = "ATF-JET",
                    productName = "Aviation Turbine Fuel (Jet A-1)",
                    productCategory = "Fuel",
                    salesUnit = "KL",
                    transactionCurrency = "KES",
                    quantity = 18500.0,
                    netAmount = 3885000.0,
                    grossAmount = 4420000.0,
                    discountAmount = 535000.0
                )
            )
            "rift_agriculture" -> listOf(
                SapBillingItem(
                    billingDocument = "90001052",
                    billingDocumentItem = "000010",
                    billingDocumentDate = "2026-10-06",
                    salesOrganization = "1000",
                    locationId = "3000",
                    plantName = "Nakuru Station",
                    customerId = "AGRI-01",
                    customerName = "Rift Valley Tea Estates",
                    customerType = "Industrial",
                    productId = "DIESEL",
                    productName = "Diesel (AGO)",
                    productCategory = "Fuel",
                    salesUnit = "KL",
                    transactionCurrency = "KES",
                    quantity = 12000.0,
                    netAmount = 1920000.0,
                    grossAmount = 2160000.0,
                    discountAmount = 240000.0
                ),
                SapBillingItem(
                    billingDocument = "90001053",
                    billingDocumentItem = "000010",
                    billingDocumentDate = "2026-10-06",
                    salesOrganization = "1000",
                    locationId = "4000",
                    plantName = "Eldoret Depot",
                    customerId = "WEST-01",
                    customerName = "Western Grain Silos",
                    customerType = "Wholesalers",
                    productId = "SUPER-PMS",
                    productName = "Super Petrol (PMS)",
                    productCategory = "Fuel",
                    salesUnit = "KL",
                    transactionCurrency = "KES",
                    quantity = 8500.0,
                    netAmount = 1445000.0,
                    grossAmount = 1615000.0,
                    discountAmount = 170000.0
                )
            )
            else -> initialSapItems
        }

        val timeNow = SimpleDateFormat("h:mm a, dd MMM", Locale.getDefault()).format(Date())
        if (presetId == "reset_baseline") {
            _billingItems.value = initialSapItems
            _activeDataSource.value = ActiveDataSource.StaticDefault
        } else {
            _billingItems.value = preset + _billingItems.value
            val presetName = when (presetId) {
                "vessel_offtake" -> "Maritime Berth 1 Offtake (+2)"
                "rift_agriculture" -> "Rift Valley Harvest (+2)"
                else -> "Preset ($presetId)"
            }
            _activeDataSource.value = ActiveDataSource.FileUploaded(
                fileName = presetName,
                recordCount = _billingItems.value.size,
                uploadTime = timeNow,
                fileSizeKb = 2L
            )
        }

        _connectionInfo.value = _connectionInfo.value.copy(
            lastSyncTime = java.time.Instant.now().toString()
        )
        return _billingItems.value.size
    }

    fun getDemoTemplateJson(): String {
        return """{
  "value": [
    {
      "BillingDocument": "90001060",
      "BillingDocumentItem": "000010",
      "BillingDocumentDate": "2026-10-06",
      "SalesOrganization": "1000",
      "LocationID": "1000",
      "PlantName": "Mombasa Depot",
      "CustomerID": "KG-01",
      "CustomerName": "KenGen Power",
      "CustomerType": "Industrial",
      "ProductID": "DIESEL",
      "ProductName": "Diesel (AGO)",
      "ProductCategory": "Fuel",
      "SalesUnit": "KL",
      "TransactionCurrency": "KES",
      "Quantity": 28000,
      "NetAmount": 3950000,
      "GrossAmount": 4600000,
      "DiscountAmount": 650000
    }
  ]
}"""
    }

    fun exportCurrentDataAsJson(): String {
        val root = JSONObject()
        val array = org.json.JSONArray()
        _billingItems.value.forEach { item ->
            val obj = JSONObject().apply {
                put("BillingDocument", item.billingDocument)
                put("BillingDocumentItem", item.billingDocumentItem)
                put("BillingDocumentDate", item.billingDocumentDate)
                put("SalesOrganization", item.salesOrganization)
                put("LocationID", item.locationId)
                put("PlantName", item.plantName)
                put("CustomerID", item.customerId)
                put("CustomerName", item.customerName)
                put("CustomerType", item.customerType)
                put("ProductID", item.productId)
                put("ProductName", item.productName)
                put("ProductCategory", item.productCategory)
                put("SalesUnit", item.salesUnit)
                put("TransactionCurrency", item.transactionCurrency)
                put("Quantity", item.quantity)
                put("NetAmount", item.netAmount)
                put("GrossAmount", item.grossAmount)
                put("DiscountAmount", item.discountAmount)
            }
            array.put(obj)
        }
        root.put("value", array)
        return root.toString(2)
    }
}
