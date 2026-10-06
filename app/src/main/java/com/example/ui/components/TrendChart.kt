package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyTrend
import com.example.ui.theme.*

@Composable
fun TrendChart(
    trends: List<DailyTrend>,
    selectedMetric: String, // "value", "volume", "margin"
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxValue = remember(trends, selectedMetric) {
        when (selectedMetric) {
            "value" -> (trends.maxOfOrNull { it.value } ?: 2.5) * 1.2
            "volume" -> (trends.maxOfOrNull { it.volumeKL.toDouble() } ?: 80.0) * 1.2
            else -> (trends.maxOfOrNull { it.marginPercent } ?: 18.0) * 1.25
        }
    }

    val selectedDay = trends.getOrNull(selectedIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        // Chart Selected Metric Readout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = when (selectedMetric) {
                        "value" -> "Daily Gross Sales (KES)"
                        "volume" -> "Daily Volume Dispatched (KL)"
                        else -> "Gross Margin Rate (%)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = when (selectedMetric) {
                        "value" -> "KES ${selectedDay?.value ?: 0.0}M"
                        "volume" -> "${selectedDay?.volumeKL ?: 0} KL"
                        else -> "${selectedDay?.marginPercent ?: 0.0}%"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = GalanaNavy
                )
            }

            selectedDay?.let {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = it.day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GalanaNavyLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Bar Chart Canvas with Touch Gestures
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(trends, selectedMetric) {
                        detectTapGestures { tapOffset ->
                            val count = trends.size
                            if (count > 0) {
                                val barSlot = size.width / count
                                val clickedIdx = (tapOffset.x / barSlot).toInt().coerceIn(0, count - 1)
                                onSelectDay(clickedIdx)
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val count = trends.size
                val barSlotWidth = w / count
                val barWidth = barSlotWidth * 0.55f

                // Draw Horizontal Grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = h * (i / gridLines.toFloat())
                    drawLine(
                        color = Color(0xFFF1F5F9),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.5f
                    )
                }

                // Draw Bars
                trends.forEachIndexed { i, item ->
                    val currentVal = when (selectedMetric) {
                        "value" -> item.value
                        "volume" -> item.volumeKL.toDouble()
                        else -> item.marginPercent
                    }

                    val normalizedHeight = ((currentVal / maxValue) * h).toFloat()
                    val barX = i * barSlotWidth + (barSlotWidth - barWidth) / 2f
                    val barY = h - normalizedHeight
                    val isSelected = i == selectedIndex

                    // Gradient for active vs standard bars
                    val barBrush = if (isSelected) {
                        Brush.verticalGradient(
                            colors = listOf(GalanaAmber, GalanaAmberDark),
                            startY = barY,
                            endY = h
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(GalanaNavyLight, GalanaNavy),
                            startY = barY,
                            endY = h
                        )
                    }

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(barX, barY),
                        size = Size(barWidth, normalizedHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Draw selection indicator dot on top
                    if (isSelected) {
                        drawCircle(
                            color = GalanaAmber,
                            radius = 4.dp.toPx(),
                            center = Offset(barX + barWidth / 2f, barY - 6.dp.toPx())
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Days labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            trends.forEachIndexed { i, item ->
                Text(
                    text = item.day.replace(" Sep", ""),
                    fontSize = 11.sp,
                    fontWeight = if (i == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (i == selectedIndex) GalanaAmberDark else TextSecondary,
                    modifier = Modifier.clickable { onSelectDay(i) }
                )
            }
        }
    }
}
