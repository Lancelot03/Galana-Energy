package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DepotTank
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DepotTankGauge(
    tank: DepotTank,
    modifier: Modifier = Modifier
) {
    val animatedFill by animateFloatAsState(
        targetValue = tank.fillPercent / 100f,
        animationSpec = tween(1000),
        label = "fill"
    )

    val tankColor = Color(tank.colorHex)

    Column(
        modifier = modifier
            .width(135.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tank.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = tank.product,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Visual Cylindrical Petroleum Tank Silhouette
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(Color(0xFFF1F5F9))
                .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 8.dp, bottomEnd = 8.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Liquid fill level
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = animatedFill)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(tankColor.copy(alpha = 0.85f), tankColor)
                        )
                    )
            )

            // Percentage label in center
            Text(
                text = "${tank.fillPercent}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (tank.fillPercent > 45) Color.White else TextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${tank.capacityKL} KL Cap",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        Text(
            text = tank.status,
            fontSize = 9.sp,
            color = if (tank.status.contains("Active") || tank.status.contains("Full")) Color(0xFF059669) else Color(0xFFD97706),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
