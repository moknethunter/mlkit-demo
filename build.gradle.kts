plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
configurations.configureEach {
    resolutionStrategy {
        force("androidx.camera:camera-core:1.4.2")
        force("androidx.camera:camera-camera2:1.4.2")
        force("androidx.camera:camera-lifecycle:1.4.2")
        force("androidx.camera:camera-view:1.4.2")
    }
}
