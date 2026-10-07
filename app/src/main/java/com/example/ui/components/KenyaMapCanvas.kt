package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationItem
import com.example.ui.theme.*

@Composable
fun KenyaMapCanvas(
    locations: List<LocationItem>,
    selectedLocation: LocationItem?,
    onSelectLocation: (LocationItem) -> Unit,
    onOpenDetails: (LocationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapMode by remember { mutableStateOf("kenya") } // "kenya" or "africa"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 26f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0A192F))
    ) {
        // Map Header & Projection Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (mapMode == "kenya") "Kenya Energy Network" else "Pan-Africa Energy Corridors",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (mapMode == "kenya") "28 Depots & Stations • Mombasa Pipeline Corridor" else "East Africa Maritime Hub & Regional Transit",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }

            // Map Mode Toggle Switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (mapMode == "kenya") GalanaNavy else Color.Transparent)
                        .clickable { mapMode = "kenya" }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Kenya",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mapMode == "kenya") Color.White else Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (mapMode == "africa") GalanaAmberDark else Color.Transparent)
                        .clickable { mapMode = "africa" }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Africa",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (mapMode == "africa") Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Interactive Map Drawing Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(locations, mapMode) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val height = size.height

                            if (mapMode == "kenya") {
                                val closest = locations.minByOrNull { loc ->
                                    val locX = (loc.coordsX / 100f) * width
                                    val locY = (loc.coordsY / 100f) * height
                                    val dx = locX - tapOffset.x
                                    val dy = locY - tapOffset.y
                                    dx * dx + dy * dy
                                }
                                if (closest != null) {
                                    val locX = (closest.coordsX / 100f) * width
                                    val locY = (closest.coordsY / 100f) * height
                                    val distSq = (locX - tapOffset.x) * (locX - tapOffset.x) + (locY - tapOffset.y) * (locY - tapOffset.y)
                                    if (distSq <= 4900f) { // ~70px touch area
                                        onSelectLocation(closest)
                                    }
                                }
                            } else {
                                // In Africa view, tapping anywhere near East Africa focuses Mombasa
                                val mombasa = locations.find { it.id == "mombasa-depot" }
                                if (mombasa != null) {
                                    onSelectLocation(mombasa)
                                }
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw background navigation graticules (Latitude / Longitude)
                val graticuleColor = Color(0x10FFFFFF)
                for (i in 1..5) {
                    val y = h * (i / 6f)
                    drawLine(graticuleColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    val x = w * (i / 6f)
                    drawLine(graticuleColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                }

                if (mapMode == "africa") {
                    // REAL CONTINENTAL AFRICA VECTOR SILHOUETTE
                    val africaPath = Path().apply {
                        // Strait of Gibraltar / North Morocco
                        moveTo(w * 0.38f, h * 0.10f)
                        // Mediterranean Coast (Algeria, Tunisia, Libya, Egypt)
                        cubicTo(w * 0.44f, h * 0.08f, w * 0.54f, h * 0.09f, w * 0.64f, h * 0.12f)
                        // Sinai & Red Sea Coastline (Egypt, Sudan, Eritrea)
                        lineTo(w * 0.70f, h * 0.16f)
                        lineTo(w * 0.74f, h * 0.28f)
                        // Horn of Africa (Djibouti, Somalia)
                        cubicTo(w * 0.82f, h * 0.32f, w * 0.88f, h * 0.38f, w * 0.82f, h * 0.46f)
                        // East African Indian Ocean Coast (Kenya, Tanzania, Mozambique)
                        cubicTo(w * 0.76f, h * 0.52f, w * 0.74f, h * 0.64f, w * 0.70f, h * 0.76f)
                        // Southern Coast to Cape of Good Hope (South Africa)
                        cubicTo(w * 0.66f, h * 0.86f, w * 0.56f, h * 0.94f, w * 0.52f, h * 0.92f)
                        // Atlantic South-West Coast (Namibia, Angola)
                        cubicTo(w * 0.46f, h * 0.86f, w * 0.44f, h * 0.74f, w * 0.44f, h * 0.64f)
                        // Bight of Benin & Gulf of Guinea (Nigeria, Cameroon, Ghana, Côte d'Ivoire)
                        cubicTo(w * 0.44f, h * 0.56f, w * 0.34f, h * 0.56f, w * 0.28f, h * 0.54f)
                        // West African Bulge (Liberia, Sierra Leone, Senegal, Mauritania)
                        cubicTo(w * 0.20f, h * 0.52f, w * 0.18f, h * 0.40f, w * 0.22f, h * 0.30f)
                        // Western Sahara & Moroccan Atlantic Coast
                        cubicTo(w * 0.26f, h * 0.22f, w * 0.32f, h * 0.14f, w * 0.38f, h * 0.10f)
                        close()
                    }

                    // Madagascar Island
                    val madagascarPath = Path().apply {
                        moveTo(w * 0.79f, h * 0.68f)
                        lineTo(w * 0.83f, h * 0.66f)
                        lineTo(w * 0.81f, h * 0.80f)
                        lineTo(w * 0.77f, h * 0.82f)
                        close()
                    }

                    // Draw Continental Landmass
                    drawPath(
                        path = africaPath,
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1E3A5F), Color(0xFF0F223C)),
                            center = Offset(w * 0.52f, h * 0.48f),
                            radius = w * 0.55f
                        )
                    )
                    drawPath(
                        path = africaPath,
                        color = Color(0x6060A5FA),
                        style = Stroke(width = 1.5f)
                    )

                    // Draw Madagascar
                    drawPath(
                        path = madagascarPath,
                        color = Color(0xFF1E3A5F)
                    )
                    drawPath(
                        path = madagascarPath,
                        color = Color(0x6060A5FA),
                        style = Stroke(width = 1.2f)
                    )

                    // Highlight Kenya & East Africa Transit Hub on Continental Map
                    val kenyaHubX = w * 0.72f
                    val kenyaHubY = w * 0.50f

                    // Maritime Oil Tanker Route from Arabian Sea to Mombasa
                    val maritimeRoute = Path().apply {
                        moveTo(w * 0.95f, h * 0.22f)
                        cubicTo(w * 0.90f, h * 0.35f, w * 0.84f, h * 0.44f, kenyaHubX, kenyaHubY)
                    }
                    drawPath(
                        path = maritimeRoute,
                        color = Color(0xFF38BDF8),
                        style = Stroke(
                            width = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )

                    // Regional Pipeline Export Lines (Into Uganda, South Sudan, Rwanda)
                    val transitRoute1 = Path().apply {
                        moveTo(kenyaHubX, kenyaHubY)
                        lineTo(kenyaHubX - 55f, kenyaHubY - 10f) // Uganda/Rwanda
                    }
                    val transitRoute2 = Path().apply {
                        moveTo(kenyaHubX, kenyaHubY)
                        lineTo(kenyaHubX - 35f, kenyaHubY - 50f) // South Sudan
                    }
                    drawPath(path = transitRoute1, color = GalanaAmberLight, style = Stroke(width = 2f))
                    drawPath(path = transitRoute2, color = GalanaAmberLight, style = Stroke(width = 2f))

                    // Pulsing beacon on Mombasa Coastal Energy Gateway
                    drawCircle(
                        color = GalanaAmberLight.copy(alpha = pulseAlpha),
                        radius = pulseRadius * 1.3f,
                        center = Offset(kenyaHubX, kenyaHubY)
                    )
                    drawCircle(
                        color = GalanaAmber,
                        radius = 8f,
                        center = Offset(kenyaHubX, kenyaHubY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.5f,
                        center = Offset(kenyaHubX, kenyaHubY)
                    )

                } else {
                    // DETAILED GEOGRAPHIC KENYA & CORRIDOR MAP
                    val kenyaTerritory = Path().apply {
                        moveTo(w * 0.26f, h * 0.20f) // North-West / Turkana
                        lineTo(w * 0.50f, h * 0.12f) // Lake Turkana North
                        lineTo(w * 0.72f, h * 0.20f) // Mandera / Ethiopia-Somalia border
                        lineTo(w * 0.86f, h * 0.44f) // Eastern border with Somalia
                        lineTo(w * 0.80f, h * 0.80f) // Malindi / Indian Ocean Coast
                        lineTo(w * 0.68f, h * 0.92f) // Mombasa Kilindini South Coast
                        lineTo(w * 0.44f, h * 0.76f) // Tanzania Southern border / Kilimanjaro
                        lineTo(w * 0.20f, h * 0.66f) // Lake Victoria shores
                        lineTo(w * 0.20f, h * 0.42f) // Western Uganda border
                        close()
                    }

                    // Shaded Territory
                    drawPath(
                        path = kenyaTerritory,
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1B355A), Color(0xFF0F1E36)),
                            center = Offset(w * 0.5f, h * 0.55f),
                            radius = w * 0.6f
                        )
                    )
                    drawPath(
                        path = kenyaTerritory,
                        color = Color(0x4060A5FA),
                        style = Stroke(width = 1.8f)
                    )

                    // Indian Ocean Coastal Waters
                    val oceanPath = Path().apply {
                        moveTo(w * 0.80f, h * 0.80f)
                        lineTo(w, h * 0.72f)
                        lineTo(w, h)
                        lineTo(w * 0.68f, h)
                        lineTo(w * 0.68f, h * 0.92f)
                        close()
                    }
                    drawPath(
                        path = oceanPath,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x350284C7), Color(0x750369A1)),
                            start = Offset(w * 0.7f, h * 0.75f),
                            end = Offset(w, h)
                        )
                    )

                    // Lake Victoria Basin (Western border)
                    drawCircle(
                        color = Color(0x450284C7),
                        radius = 32f,
                        center = Offset(w * 0.18f, h * 0.58f)
                    )

                    // Lake Turkana Basin (Northern border)
                    val turkanaPath = Path().apply {
                        moveTo(w * 0.38f, h * 0.15f)
                        lineTo(w * 0.44f, h * 0.14f)
                        lineTo(w * 0.42f, h * 0.34f)
                        lineTo(w * 0.37f, h * 0.33f)
                        close()
                    }
                    drawPath(path = turkanaPath, color = Color(0x400284C7))

                    // Kenya Pipeline Company (KPC) Strategic Supply Pipeline Corridor
                    val pipelinePoints = listOf(
                        Offset(w * 0.74f, h * 0.84f), // Mombasa
                        Offset(w * 0.52f, h * 0.635f), // Nairobi
                        Offset(w * 0.38f, h * 0.54f),  // Nakuru
                        Offset(w * 0.28f, h * 0.44f),  // Eldoret
                        Offset(w * 0.26f, h * 0.55f)   // Kisumu
                    )

                    val pipelinePath = Path().apply {
                        moveTo(pipelinePoints[0].x, pipelinePoints[0].y)
                        for (i in 1 until pipelinePoints.size) {
                            lineTo(pipelinePoints[i].x, pipelinePoints[i].y)
                        }
                    }

                    // Dotted Energy Pipeline Route
                    drawPath(
                        path = pipelinePath,
                        color = Color(0xFF38BDF8),
                        style = Stroke(
                            width = 3.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    // Draw Each Station & Depot Marker
                    locations.forEach { loc ->
                        val cx = (loc.coordsX / 100f) * w
                        val cy = (loc.coordsY / 100f) * h
                        val isSelected = selectedLocation?.id == loc.id
                        val baseColor = if (loc.type == "Depot") GalanaAmberLight else FuelDieselBlue

                        // Pulsing beacon on Mombasa or selected item
                        if (loc.highlight || isSelected) {
                            drawCircle(
                                color = baseColor.copy(alpha = pulseAlpha),
                                radius = pulseRadius,
                                center = Offset(cx, cy)
                            )
                        }

                        // Outer marker ring
                        drawCircle(
                            color = if (isSelected) Color.White else baseColor.copy(alpha = 0.4f),
                            radius = if (isSelected) 13f else if (loc.type == "Depot") 10f else 7.5f,
                            center = Offset(cx, cy)
                        )

                        // Core marker dot
                        drawCircle(
                            color = baseColor,
                            radius = if (isSelected) 7.5f else if (loc.type == "Depot") 6f else 4.5f,
                            center = Offset(cx, cy)
                        )

                        // Inner white core
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 3.5f else 2.5f,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }

        // Selected Location Card
        selectedLocation?.let { loc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (loc.type == "Depot") GalanaAmber.copy(alpha = 0.15f)
                                    else FuelDieselBlue.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (loc.type == "Depot") GalanaAmberDark else FuelDieselBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = loc.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = loc.type,
                                    fontSize = 10.sp,
                                    color = if (loc.type == "Depot") GalanaAmberDark else FuelDieselBlue,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            if (loc.type == "Depot") Color(0xFFFEF3C7) else Color(0xFFE0F2FE),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "KES ${loc.salesKES}M",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GalanaNavy
                                )
                                Text(
                                    text = "• ${loc.volumeKL} KL",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "▲ ${loc.change}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FuelPetrolGreen
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onOpenDetails(loc) },
                        colors = ButtonDefaults.buttonColors(containerColor = GalanaNavy),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Inspect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
