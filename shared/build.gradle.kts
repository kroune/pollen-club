plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    applyDefaultHierarchyTemplate()

    android {
        namespace = "io.github.kroune.pollen.shared"
        compileSdk = libs.versions.android.compile.sdk
            .get()
            .toInt()
        minSdk = libs.versions.android.min.sdk
            .get()
            .toInt()
        androidResources.enable = true
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.collections.immutable)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)

            implementation(libs.androidx.datastore.preferences)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.framework)

            implementation(libs.kermit)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.navigation3.ui)

            implementation(libs.vico.compose.m3)
        }
        commonTest.dependencies {
            api(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation(libs.compose.uiTooling)
            implementation(libs.google.maps.compose)
            implementation(libs.google.maps.compose.utils)
            implementation(libs.play.services.maps)
            implementation(libs.play.services.location)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
