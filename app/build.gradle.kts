plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.janhvi.qrshare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.janhvi.qrshare"
        minSdk = 26
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
//    implementation(libs.play.services.mlkit.barcode.scanning)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //for image slider
//    implementation (libs.imageslideshow)
    implementation(libs.core)
    implementation(libs.ccp)

    implementation (libs.play.services.maps)


}