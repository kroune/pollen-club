plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.mokoResources)
}

room {
    schemaDirectory("$projectDir/schemas")
}

multiplatformResources {
    resourcesPackage.set("io.github.kroune.pollen")
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

    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.ui.backhandler)

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
            implementation(libs.androidx.datastore.core.okio)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            implementation(libs.kermit)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.compose.media.player)

            implementation(libs.navigation3.ui)

            implementation(libs.vico.compose.m3)

            implementation(libs.qrose)
            api(projects.qrApi)
            api(projects.theme)

            api(libs.moko.resources)
            api(libs.moko.resources.compose)
        }
        commonTest.dependencies {
            api(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
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
    add("kspJvm", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
