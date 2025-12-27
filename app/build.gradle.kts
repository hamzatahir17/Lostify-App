import java.util.Properties

plugins {
    alias(libs.plugins.android.application)

    alias(libs.plugins.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.lostify"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lostify"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Cloudinary Config
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${localProperties.getProperty("CLOUDINARY_CLOUD_NAME")}\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"${localProperties.getProperty("CLOUDINARY_API_KEY")}\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${localProperties.getProperty("CLOUDINARY_API_SECRET")}\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    implementation(platform(libs.firebase.bom))


    implementation(libs.firebase.auth)


    implementation("com.google.firebase:firebase-firestore")


    implementation("com.google.android.gms:play-services-auth:20.5.0")


    implementation("com.cloudinary:cloudinary-android:2.5.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}