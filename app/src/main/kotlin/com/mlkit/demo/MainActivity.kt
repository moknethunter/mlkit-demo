package com.mlkit.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mlkit.demo.camera.CameraAnalyzer
import com.mlkit.demo.camera.CameraManager
import com.mlkit.demo.mlkit.ObjectDetectionAnalyzer
import com.mlkit.demo.ui.BoundingBoxOverlay
import com.mlkit.demo.ui.grayscale
import com.mlkit.demo.viewmodel.DemoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CameraScreen()
                }
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasCameraPermission = isGranted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        CameraPreview()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required")
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CameraPreview() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current
    val cameraManager: CameraManager = koinInject()
    val objectDetectionAnalyzer: ObjectDetectionAnalyzer = koinInject()
    val viewModel: DemoViewModel = viewModel()
    val detectionState by viewModel.detectionState.collectAsState()

    // scope خاص بالتحليل — يُلغى عند الخروج من الشاشة
    val analyzerScope = remember {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    DisposableEffect(Unit) {
        onDispose { analyzerScope.cancel() }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        // أبعاد الواجهة كـ provider — تُقرأ في لحظة التحليل
        val viewWidthProvider = { with(density) { maxWidth.toPx().toInt() } }
        val viewHeightProvider = { with(density) { maxHeight.toPx().toInt() } }

        val cameraAnalyzer = remember(objectDetectionAnalyzer, viewModel) {
            CameraAnalyzer(
                objectDetectionAnalyzer = objectDetectionAnalyzer,
                viewModel = viewModel,
                viewWidthProvider = viewWidthProvider,
                viewHeightProvider = viewHeightProvider,
                scope = analyzerScope
            )
        }

        // 1) الفيديو (رمادي) + المربعات
        CameraXPreviewView(
            cameraManager = cameraManager,
            lifecycleOwner = lifecycleOwner,
            cameraAnalyzer = cameraAnalyzer,
            modifier = Modifier
                .fillMaxSize()
                .grayscale()   // 🔑 الفيديو رمادي
        )

        // 2) طبقة المربعات الخضراء — لا تتأثر بالفلتر لأنها بعد AndroidView
        detectionState?.let { state ->
            BoundingBoxOverlay(
                label = state.label,
                confidence = state.confidence,
                boundingBox = state.boundingBox
            )
        }
    }
}

@Composable
internal fun CameraXPreviewView(
    cameraManager: CameraManager,
    lifecycleOwner: LifecycleOwner,
    cameraAnalyzer: CameraAnalyzer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 🔑 COMPATIBLE = TextureView، ضروري ليعمل فلتر الرمادي
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(cameraAnalyzer) {
        cameraManager.startCamera(lifecycleOwner, previewView, cameraAnalyzer)
    }

    DisposableEffect(Unit) {
        onDispose { cameraManager.shutdown() }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}
