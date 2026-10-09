package com.mlkit.demo.viewmodel

import androidx.lifecycle.ViewModel
import com.mlkit.demo.model.BoundingBox
import com.mlkit.demo.model.DetectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DemoViewModel : ViewModel() {
    // الآن List من 5 كائنات
    private val _detectionState = MutableStateFlow<List<DetectionState>>(emptyList())
    val detectionState: StateFlow<List<DetectionState>> = _detectionState.asStateFlow()

    // تحديث فوري كل فريم - بدون delay وبدون Cooldown
    fun onObjectsDetected(detections: List<Triple<String, Float, BoundingBox>>) {
        _detectionState.value = detections.map { (label, conf, box) ->
            DetectionState(label, conf, box)
        }.take(5)
    }

    // للتوافق مع الكود القديم - يحول كائن واحد الى List
    fun onObjectDetected(
        label: String,
        confidence: Float,
        boundingBox: BoundingBox
    ) {
        onObjectsDetected(listOf(Triple(label, confidence, boundingBox)))
    }

    fun clearDetection() {
        _detectionState.value = emptyList()
    }
}
