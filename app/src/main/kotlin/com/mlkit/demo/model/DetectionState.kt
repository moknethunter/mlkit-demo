package com.mlkit.demo.model

// حالة الشاشة - الآن تحتوي قائمة من الكائنات
data class DetectionState(
    val detections: List<Detection> = emptyList(),
    val isAnalyzing: Boolean = false
)

// كائن واحد داخل القائمة
data class Detection(
    val label: String,
    val confidence: Float,
    val boundingBox: BoundingBox,
    val trackingId: Int? = null
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)
