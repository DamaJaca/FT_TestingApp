@file:Suppress("DEPRECATION")

plugins {

    kotlin("kapt")
    id ("com.google.dagger.hilt.android")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id ("androidx.navigation.safeargs.kotlin")

}
configurations.all {
    resolutionStrategy {
        force ("org.tensorflow:tensorflow-lite:2.12.0")
    }
}

android {
    namespace = "com.djc.ft_testingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.djc.ft_testingapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    kapt{
        correctErrorTypes=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    aaptOptions {
        noCompress ("tflite")
    }


}

dependencies {
    //Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    implementation ("com.google.guava:guava:31.1-android")

    //TensorFlow
    implementation (libs.tensorflow.lite)
    implementation (libs.tensorflow.lite.support)

    //CameraX
    implementation ("androidx.camera:camera-camera2:1.4.1")
    implementation ("androidx.camera:camera-lifecycle:1.4.1")
    implementation ("androidx.camera:camera-view:1.4.1")

    //ML Face Detection
    implementation ("com.google.mlkit:face-detection:16.1.7")

    //Dagger hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.room.ktx)
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}