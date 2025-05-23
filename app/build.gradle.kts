plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.quizflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quizflow"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // added dependencies
    implementation(libs.circleimageview)
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.glideTransformations)
    implementation(libs.chipNavbar)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2ConverterGson)
    implementation(libs.okhttp3)
    implementation(libs.stompClient)
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}