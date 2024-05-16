plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.quickresponsecode"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quickresponsecode"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Hilt Android Processor - https://mvnrepository.com/artifact/com.google.dagger/hilt-android-compiler
    // Dependency injection with Hilt - https://developer.android.com/training/dependency-injection/hilt-android#setup
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.fragment)


    // Get started with the Navigation component - https://developer.android.com/guide/navigation/get-started#Set-up
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)


    // Preferences DataStore - https://developer.android.com/topic/libraries/architecture/datastore#preferences-datastore-dependencies
    implementation(libs.androidx.datastore.preferences)


    // Get started with Camera X - https://developer.android.com/codelabs/camerax-getting-started#0
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)


    // Scan barcodes with ML Kit on Android - https://developers.google.com/ml-kit/vision/barcode-scanning/android#kotlin
    implementation(libs.barcode.scanning)

    // Consume flows safely in Jetpack Compose - https://medium.com/androiddevelopers/consuming-flows-safely-in-jetpack-compose-cde014d0d5a3
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ZXing for create QR Code- https://github.com/zxing/zxing
    implementation(libs.core)

    // Save data in a local database using Room - https://developer.android.com/training/data-storage/room#setup
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Coil for image loading - https://coil-kt.github.io/coil/
    implementation(libs.coil)
    implementation(libs.coil.compose)
}

// Dependency injection with Hilt - Allow references to generated code
kapt {
    correctErrorTypes = true
}