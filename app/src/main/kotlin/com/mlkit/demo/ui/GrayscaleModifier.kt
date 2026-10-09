package com.mlkit.demo.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint

/**
 * يُطبّق فلتر Grayscale (رمادي) على أي Composable.
 *
 * - يستخدم ColorMatrix لجعل التشبّع (saturation) = 0.
 * - يعمل على جميع إصدارات Android.
 *
 * ⚠️ شرط أساسي: يُستخدم مع PreviewView في وضع COMPATIBLE (TextureView)،
 * لأن SurfaceView (وضع PERFORMANCE) لا يقبل فلاتر الألوان.
 */
fun Modifier.grayscale(): Modifier = drawWithContent {
    val paint = Paint().apply {
        colorFilter = ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(0f) }
        )
    }
    // احفظ طبقة off-screen، ارسم المحتوى داخلها بالفلتر، ثم استعد
    drawContext.canvas.saveLayer(Rect(Offset.Zero, size), paint)
    this@drawWithContent.drawContent()
    drawContext.canvas.restore()
}
