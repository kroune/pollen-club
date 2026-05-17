plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.roborazziPlugin)
}

val jdkVersion = libs.versions.java
    .get()
    .toInt()

android {
    namespace = "io.github.kroune.pollen"
    compileSdk = libs.versions.android.compile.sdk
        .get()
        .toInt()

    defaultConfig {
        applicationId = "io.github.kroune.pollen"
        minSdk = libs.versions.android.min.sdk
            .get()
            .toInt()
        targetSdk = libs.versions.android.compile.sdk
            .get()
            .toInt()
        versionCode = libs.versions.app.version.code
            .get()
            .toInt()
        versionName = libs.versions.app.version.name
            .get()
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-dev"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(jdkVersion)
        targetCompatibility = JavaVersion.toVersion(jdkVersion)
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.koin.core)

    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.composable.preview.scanner)
    testImplementation(libs.coil.test)
    testImplementation(libs.coil.compose)
    testImplementation(libs.androidx.ui.test.junit4)
}
