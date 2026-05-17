plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "io.github.kroune.pollen.qr.foss"
    compileSdk = libs.versions.android.compile.sdk
        .get()
        .toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk
            .get()
            .toInt()
    }
    buildFeatures { compose = true }
    compileOptions {
        val jdkVersion = libs.versions.java.get().toInt()
        sourceCompatibility = JavaVersion.toVersion(jdkVersion)
        targetCompatibility = JavaVersion.toVersion(jdkVersion)
    }
}

dependencies {
    implementation(projects.qrApi)
    implementation(projects.theme)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.koin.core)
    implementation(libs.kermit)
    implementation(libs.androidx.activity.compose)
}
