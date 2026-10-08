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
import com.mlkit.demo.model.DetectionState

private val green = Color(0xFF00FF00)

// هذا الجديد: يرسم 5 مربعات خضراء في Canvas واحد - أسرع بكثير
@Composable
fun BoundingBoxOverlay(
    detections: List<DetectionState>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Box(modifier = modifier.fillMaxSize()) {
        // رسم كل المربعات في Canvas واحد
        Canvas(modifier = Modifier.fillMaxSize()) {
            detections.forEach { detection ->
                val box = detection.boundingBox
                // ظل متوهج
                drawRoundRect(
                    color = green.copy(alpha = 0.25f),
                    topLeft = Offset(box.left - 6, box.top - 6),
                    size = Size(box.right - box.left + 12, box.bottom - box.top + 12),
                    cornerRadius = CornerRadius(20f, 20f),
                    style = Stroke(width = 14f)
                )
                // المربع الأساسي يتحرك مع الكائن
                drawRoundRect(
                    color = green,
                    topLeft = Offset(box.left, box.top),
                    size = Size(box.right - box.left, box.bottom - box.top),
                    cornerRadius = CornerRadius(16f, 16f),
                    style = Stroke(width = 5f)
                )
            }
        }
        // النصوص فوق كل مربع
        detections.forEach { detection ->
            val box = detection.boundingBox
            val labelOffsetX = box.left
            val labelOffsetY = (box.top - 46).coerceAtLeast(6f)
            val confidencePercent = (detection.confidence * 100).toInt()
            
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
                    text = "${detection.label} $confidencePercent%",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// للتوافق مع الكود القديم - يستدعي الجديد
@Composable
fun SingleBoundingBoxOverlay(
    label: String,
    confidence: Float,
    boundingBox: BoundingBox,
    modifier: Modifier = Modifier
) {
    BoundingBoxOverlay(
        detections = listOf(DetectionState(label, confidence, boundingBox)),
        modifier = modifier
    )
}
