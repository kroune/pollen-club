package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.kroune.pollen.data.local.db.appContext

actual fun createPlatformDataStore(): DataStore<Preferences> = createDataStore {
    appContext.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
}
