package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual fun createPlatformDataStore(): DataStore<Preferences> = createDataStore {
    File(System.getProperty("java.io.tmpdir"), DATASTORE_FILE_NAME).absolutePath
}