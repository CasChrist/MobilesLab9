plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.retrofitforecaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.retrofitforecaster"
        minSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.recyclerview.v130)
    implementation(libs.androidx.lifecycle.runtime.ktx.v260)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v190)
    implementation(libs.glide)
    implementation(libs.compiler)
    implementation(libs.picasso.v271828)
    implementation(libs.timber)
}