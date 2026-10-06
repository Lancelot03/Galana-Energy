package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TrendingUp
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
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F1E36))
    ) {
        // Map Header & Info bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Kenya Energy Network",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "28 Depots & Stations • Mombasa Pipeline Corridor",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
            // Map Legend pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GalanaAmberLight)
                )
                Text("Depot", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FuelDieselBlue)
                )
                Text("Station", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        // Interactive Map Drawing Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(locations) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val height = size.height
                            // Find closest location to tap
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
                                if (distSq <= 4000f) { // ~63px radius
                                    onSelectLocation(closest)
                                }
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Draw background grid lines (latitude/longitude subtle grid)
                val gridColor = Color(0x15FFFFFF)
                for (i in 1..4) {
                    val y = h * (i / 5f)
                    drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                    val x = w * (i / 5f)
                    drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                }

                // Decorative Kenya Territory stylized boundary
                val kenyaPath = Path().apply {
                    moveTo(w * 0.28f, h * 0.18f) // North-West border
                    lineTo(w * 0.52f, h * 0.12f) // Lake Turkana North
                    lineTo(w * 0.72f, h * 0.22f) // North-East
                    lineTo(w * 0.88f, h * 0.44f) // Somalia border
                    lineTo(w * 0.82f, h * 0.82f) // Indian Ocean Coastline
                    lineTo(w * 0.68f, h * 0.90f) // Mombasa South Coast
                    lineTo(w * 0.45f, h * 0.76f) // Tanzania Southern border
                    lineTo(w * 0.22f, h * 0.64f) // Lake Victoria
                    lineTo(w * 0.22f, h * 0.42f) // Uganda border
                    close()
                }

                drawPath(
                    path = kenyaPath,
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF162A4A), Color(0xFF0F1E36)),
                        center = Offset(w * 0.5f, h * 0.55f),
                        radius = w * 0.6f
                    )
                )
                drawPath(
                    path = kenyaPath,
                    color = Color(0x3560A5FA),
                    style = Stroke(width = 1.5f)
                )

                // Draw Indian Ocean coastline waters indicator
                val oceanPath = Path().apply {
                    moveTo(w * 0.82f, h * 0.82f)
                    lineTo(w, h * 0.70f)
                    lineTo(w, h)
                    lineTo(w * 0.68f, h)
                    lineTo(w * 0.68f, h * 0.90f)
                    close()
                }
                drawPath(
                    path = oceanPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x300284C7), Color(0x600369A1)),
                        start = Offset(w * 0.7f, h * 0.7f),
                        end = Offset(w, h)
                    )
                )

                // Draw Lake Victoria indicator on West
                drawCircle(
                    color = Color(0x350284C7),
                    radius = 28f,
                    center = Offset(w * 0.20f, h * 0.58f)
                )

                // Draw Kenya Pipeline Corridor Line: Mombasa -> Nairobi -> Nakuru -> Eldoret -> Kisumu
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

                // Dotted Pipeline route
                drawPath(
                    path = pipelinePath,
                    color = Color(0xFF38BDF8),
                    style = Stroke(
                        width = 3.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )

                // Draw Each Location Node
                locations.forEach { loc ->
                    val cx = (loc.coordsX / 100f) * w
                    val cy = (loc.coordsY / 100f) * h
                    val isSelected = selectedLocation?.id == loc.id

                    val baseColor = if (loc.type == "Depot") GalanaAmberLight else FuelDieselBlue

                    // Pulsing effect for Mombasa Depot or selected item
                    if (loc.highlight || isSelected) {
                        drawCircle(
                            color = baseColor.copy(alpha = pulseAlpha),
                            radius = pulseRadius,
                            center = Offset(cx, cy)
                        )
                    }

                    // Outer halo
                    drawCircle(
                        color = if (isSelected) Color.White else baseColor.copy(alpha = 0.35f),
                        radius = if (isSelected) 12f else if (loc.type == "Depot") 10f else 8f,
                        center = Offset(cx, cy)
                    )

                    // Core marker
                    drawCircle(
                        color = baseColor,
                        radius = if (isSelected) 7f else if (loc.type == "Depot") 6f else 4.5f,
                        center = Offset(cx, cy)
                    )

                    // Inner dot
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 3.5f else 2.5f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }

        // Selected Location Card
        selectedLocation?.let { loc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (loc.type == "Depot") GalanaAmber.copy(alpha = 0.15f) else FuelDieselBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (loc.type == "Depot") GalanaAmberDark else FuelDieselBlue,
                                modifier = Modifier.size(20.dp)
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
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
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
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Inspect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
