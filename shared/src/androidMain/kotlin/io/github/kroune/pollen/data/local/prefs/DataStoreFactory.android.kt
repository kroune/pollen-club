package io.github.kroune.pollen.data.local.prefs

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object AndroidPlatformDeps : KoinComponent {
    val context: Context get() = get()
}

actual fun platformFilePath(fileName: String): String =
    AndroidPlatformDeps.context.filesDir.resolve(fileName).absolutePath
