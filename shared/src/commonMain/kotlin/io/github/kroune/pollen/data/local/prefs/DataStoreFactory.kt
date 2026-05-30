package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/** Absolute path for a DataStore file in the platform's app-private storage directory. */
expect fun platformFilePath(fileName: String): String

fun createPlatformDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { platformFilePath(PREFS_FILE_NAME).toPath() },
    )

const val PREFS_FILE_NAME = "pollen_prefs.preferences_pb"
