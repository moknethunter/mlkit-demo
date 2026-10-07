plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mlkit.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mlkit.demo"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.activity)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.google.mlkit.obj.detection)
    implementation(libs.google.mlkit.image.labeling)
    implementation(libs.google.mlkit.vision.common)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    dependencies {
    // CameraX - يحل محل مكتبة ML Kit الأصلية غير المتوافقة مع 16KB
    implementation "androidx.camera:camera-core:1.4.2"
    implementation "androidx.camera:camera-camera2:1.4.2"
    implementation "androidx.camera:camera-lifecycle:1.4.2"
    implementation "androidx.camera:camera-view:1.4.2"

    // ML Kit - كشف الكائنات
    implementation 'com.google.mlkit:object-detection:17.0.0'

    // AndroidX الأساسية
    implementation 'androidx.core:core-ktx:1.13.1'
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // اختبارات (احتفظ بها أو احذفها حسب الحاجة)
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.2.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
}
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
