package com.mlkit.demo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mlkit.demo.model.BoundingBox

@Composable
fun BoundingBoxOverlay(
    label: String,
    confidence: Float,
    boundingBox: BoundingBox,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val confidencePercent = (confidence * 100).toInt()
    val green = Color(0xFF00FF00)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // مربع أخضر سميك مع ظل بسيط للتتبع
            drawRoundRect(
                color = green.copy(alpha = 0.3f),
                topLeft = Offset(boundingBox.left-4, boundingBox.top-4),
                size = Size(
                    boundingBox.right - boundingBox.left + 8,
                    boundingBox.bottom - boundingBox.top + 8
                ),
                cornerRadius = CornerRadius(20f, 20f),
                style = Stroke(width = 12f)
            )
            drawRoundRect(
                color = green,
                topLeft = Offset(boundingBox.left, boundingBox.top),
                size = Size(
                    boundingBox.right - boundingBox.left,
                    boundingBox.bottom - boundingBox.top
                ),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 6f)
            )
        }

        val labelOffsetX = boundingBox.left
        val labelOffsetY = (boundingBox.top - 48).coerceAtLeast(8f)

        Box(
            modifier = Modifier
                .offset(
                    x = with(density) { labelOffsetX.toDp() },
                    y = with(density) { labelOffsetY.toDp() }
                )
                .background(green, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$label $confidencePercent%",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
