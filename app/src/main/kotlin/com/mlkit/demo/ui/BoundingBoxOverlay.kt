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

// هذا يرسم مربع واحد - يتحرك ويتغير حجمه تلقائياً مع الكائن
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
            // ظل متوهج خلف المربع
            drawRoundRect(
                color = green.copy(alpha = 0.25f),
                topLeft = Offset(boundingBox.left - 6, boundingBox.top - 6),
                size = Size(
                    boundingBox.right - boundingBox.left + 12,
                    boundingBox.bottom - boundingBox.top + 12
                ),
                cornerRadius = CornerRadius(20f, 20f),
                style = Stroke(width = 14f)
            )
            // المربع الأخضر الأساسي - حجمه = حجم الكائن بالضبط
            drawRoundRect(
                color = green,
                topLeft = Offset(boundingBox.left, boundingBox.top),
                size = Size(
                    boundingBox.right - boundingBox.left,
                    boundingBox.bottom - boundingBox.top
                ),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 5f)
            )
        }

        val labelOffsetX = boundingBox.left
        val labelOffsetY = (boundingBox.top - 46).coerceAtLeast(6f)

        Box(
            modifier = Modifier
                .offset(
                    x = with(density) { labelOffsetX.toDp() },
                    y = with(density) { labelOffsetY.toDp() }
                )
                .background(green, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$label $confidencePercent%",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// **** هذا الجديد: يرسم مربعات لكل الكائنات في الصورة ****
@Composable
fun MultiBoundingBoxOverlay(
    detections: List<Triple<String, Float, BoundingBox>>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        detections.forEach { (label, conf, box) ->
            BoundingBoxOverlay(
                label = label,
                confidence = conf,
                boundingBox = box
            )
        }
    }
}
