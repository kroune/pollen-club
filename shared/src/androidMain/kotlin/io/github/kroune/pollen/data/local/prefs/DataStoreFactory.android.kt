package io.github.kroune.pollen.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private object AndroidPlatformDeps : KoinComponent {
    val context: Context get() = get()
}

actual fun createPlatformDataStore(): DataStore<Preferences> = createDataStore {
    AndroidPlatformDeps.context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
}
