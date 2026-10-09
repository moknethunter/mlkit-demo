package com.mlkit.demo.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

/** مدة تلطيف حركة المربع بين إطارات ML Kit (بالمللي ثانية) */
private const val BOX_ANIM_MS = 80

/**
 * يرسم مربّعًا أخضر متحركًا مع ملصق فوقه.
 * الإحداثيات تُحرَّك بسلاسة عبر animateFloatAsState.
 */
@Composable
fun BoundingBoxOverlay(
    label: String,
    confidence: Float,
    boundingBox: BoundingBox,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // ---- حركة سلسة لكل إحداثي ----
    val left by animateFloatAsState(
        targetValue = boundingBox.left,
        animationSpec = tween(BOX_ANIM_MS, easing = LinearEasing),
        label = "left"
    )
    val top by animateFloatAsState(
        targetValue = boundingBox.top,
        animationSpec = tween(BOX_ANIM_MS, easing = LinearEasing),
        label = "top"
    )
    val right by animateFloatAsState(
        targetValue = boundingBox.right,
        animationSpec = tween(BOX_ANIM_MS, easing = LinearEasing),
        label = "right"
    )
    val bottom by animateFloatAsState(
        targetValue = boundingBox.bottom,
        animationSpec = tween(BOX_ANIM_MS, easing = LinearEasing),
        label = "bottom"
    )

    val confidencePercent = (confidence * 100).toInt()

    // اللون الأخضر — واضح في الوضع الليلي والنهاري
    val boxColor = if (isSystemInDarkTheme()) Color(0xFF00E676) else Color(0xFF00C853)
    val labelTextColor = Color.Black

    // قياسات dp → px
    val glowStrokePx  = with(density) { 7.dp.toPx() }
    val mainStrokePx  = with(density) { 2.5.dp.toPx() }
    val glowCornerPx  = with(density) { 10.dp.toPx() }
    val mainCornerPx  = with(density) { 8.dp.toPx() }
    val glowPaddingPx = with(density) { 3.dp.toPx() }
    val labelGapPx    = with(density) { 30.dp.toPx() }

    val boxWidth  = right - left
    val boxHeight = bottom - top

    Box(modifier = modifier.fillMaxSize()) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            // 1) توهّج أخضر خلفي
            drawRoundRect(
                color = boxColor.copy(alpha = 0.25f),
                topLeft = Offset(left - glowPaddingPx, top - glowPaddingPx),
                size = Size(boxWidth + glowPaddingPx * 2, boxHeight + glowPaddingPx * 2),
                cornerRadius = CornerRadius(glowCornerPx, glowCornerPx),
                style = Stroke(width = glowStrokePx)
            )
            // 2) المربع الأساسي
            drawRoundRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(mainCornerPx, mainCornerPx),
                style = Stroke(width = mainStrokePx)
            )
        }

        // 3) الملصق
        val labelOffsetX = left
        val labelOffsetY = (top - labelGapPx).coerceAtLeast(0f)

        Box(
            modifier = Modifier
                .offset(
                    x = with(density) { labelOffsetX.toDp() },
                    y = with(density) { labelOffsetY.toDp() }
                )
                .background(boxColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$label $confidencePercent%",
                color = labelTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * لرسم عدة كشوفات في نفس الوقت.
 */
@Composable
fun MultiBoundingBoxOverlay(
    detections: List<DetectionState>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        detections.forEach { state ->
            BoundingBoxOverlay(
                label = state.label,
                confidence = state.confidence,
                boundingBox = state.boundingBox
            )
        }
    }
}
