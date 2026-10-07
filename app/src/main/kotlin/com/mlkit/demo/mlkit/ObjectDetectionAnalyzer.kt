package com.mlkit.demo.mlkit

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import com.mlkit.demo.mlkit.model.DetectedObject
import com.mlkit.demo.mlkit.utils.toCustomBitmap
import com.mlkit.demo.mlkit.vision.ImageLabelingAnalyzer
import com.mlkit.demo.mlkit.vision.ObjectDetectionDetector
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow
import kotlin.math.sqrt

data class ObjectDetectionResult(
    val label: String,
    val confidence: Float,
    val boundingBox: Rect,
    val trackingId: Int? = null
)

internal class ObjectDetectionAnalyzer(
    private val detector: ObjectDetectionDetector,
    private val imageLabelingAnalyzer: ImageLabelingAnalyzer,
) {

    // الآن نتتبع 5 كائنات
    suspend fun analyze(imageProxy: ImageProxy): List<ObjectDetectionResult> {
        var bitmap: Bitmap? = null
        try {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            bitmap = imageProxy.toCustomBitmap() ?: return emptyList()

            val detectedObjects = detector.detectObjects(bitmap, rotationDegrees) ?: return emptyList()
            if (detectedObjects.isEmpty()) return emptyList()

            // اختر أفضل 5 كائنات حسب القرب من المركز والحجم
            val topObjects = selectTopNObjects(
                detectedObjects = detectedObjects,
                imageWidth = imageProxy.width,
                imageHeight = imageProxy.height,
                n = 5
            )

            val results = mutableListOf<ObjectDetectionResult>()

            for (obj in topObjects) {
                var croppedBitmap: Bitmap? = null
                try {
                    val boundingBox = obj.boundingBox
                    // تأكد أن القص داخل حدود الصورة
                    val left = boundingBox.left.coerceAtLeast(0)
                    val top = boundingBox.top.coerceAtLeast(0)
                    val width = boundingBox.width().coerceAtMost(bitmap.width - left).coerceAtLeast(1)
                    val height = boundingBox.height().coerceAtMost(bitmap.height - top).coerceAtLeast(1)

                    croppedBitmap = Bitmap.createBitmap(bitmap, left, top, width, height)

                    val labelResult = imageLabelingAnalyzer.analyzeImage(
                        croppedBitmap = croppedBitmap,
                        rotationDegrees = rotationDegrees,
                    ) ?: continue

                    results.add(
                        ObjectDetectionResult(
                            label = labelResult.label,
                            confidence = labelResult.confidence,
                            boundingBox = obj.boundingBox,
                            trackingId = obj.trackingId
                        )
                    )
                } catch (e: Exception) {
                    Log.e("MLKitDemo", "Labeling failed for one object", e)
                } finally {
                    croppedBitmap?.recycle()
                }
            }
            return results

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("MLKitDemo", "ML Kit analysis failed", e)
            return emptyList()
        } finally {
            bitmap?.recycle()
        }
    }

    private fun selectTopNObjects(
        detectedObjects: List<DetectedObject>,
        imageWidth: Int,
        imageHeight: Int,
        n: Int
    ): List<DetectedObject> {
        return detectedObjects
            .map { ScoredObject(it, calculateScore(it, imageWidth, imageHeight)) }
           
