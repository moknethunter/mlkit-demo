package com.mlkit.demo.camera

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraManager(private val context: Context) {

    companion object {
        private const val TAG = "CameraManager"
    }

    private var cameraProvider: ProcessCameraProvider? = null

    // خيط واحد لتحليل الإطارات — يبقى حيًا لأن الكنترولر Koin Singleton
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        analyzer: ImageAnalysis.Analyzer? = null
    ) {
        val provider = getCameraProvider()

        // فك أي ربط سابق
        provider.unbindAll()

        // 1) Preview
        val preview = Preview.Builder()
            .build()
            .also { it.surfaceProvider = previewView.surfaceProvider }

        // 2) ImageAnalysis (اختياري)
        val imageAnalysis = analyzer?.let {
            ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor, it)
                }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            if (imageAnalysis != null) {
                provider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            } else {
                provider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Camera binding failed", e)
        }
    }

    /**
     * يفصل الكاميرا فقط.
     * لا نُغلق الـ executor لأن الكنترولر Singleton وقد يُعاد استخدامه
     * عند العودة إلى الشاشة.
     */
    fun shutdown() {
        try {
            cameraProvider?.unbindAll()
        } catch (e: Exception) {
            Log.e(TAG, "Unbind failed", e)
        }
    }

    private suspend fun getCameraProvider(): ProcessCameraProvider {
        return cameraProvider ?: suspendCoroutine { continuation ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                val provider = future.get()
                cameraProvider = provider
                continuation.resume(provider)
            }, ContextCompat.getMainExecutor(context))
        }
    }
}
