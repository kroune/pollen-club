package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
    )

expect fun createPlatformDataStore(): DataStore<Preferences>

const val DATASTORE_FILE_NAME = "pollen_prefs.preferences_pb"
