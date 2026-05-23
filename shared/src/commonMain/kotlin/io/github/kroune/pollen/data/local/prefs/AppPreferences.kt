package io.github.kroune.pollen.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.kroune.pollen.domain.model.AppLocale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    val acceptedTerms: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ACCEPTED] ?: false
    }

    val isFirstRun: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_FIRST_RUN] ?: true
    }

    val languageCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: AppLocale.Default.tag
    }

    val selectedTags: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_TAGS] ?: ""
    }

    suspend fun setAcceptedTerms(accepted: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_ACCEPTED] = accepted }
    }

    suspend fun setFirstRun(firstRun: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_FIRST_RUN] = firstRun }
    }

    suspend fun setLanguageCode(code: String) {
        dataStore.edit { prefs -> prefs[KEY_LANGUAGE] = code }
    }

    suspend fun setSelectedTags(tags: String) {
        dataStore.edit { prefs -> prefs[KEY_TAGS] = tags }
    }

    private companion object {
        val KEY_ACCEPTED = booleanPreferencesKey("accepted_terms")
        val KEY_FIRST_RUN = booleanPreferencesKey("first_run")
        val KEY_LANGUAGE = stringPreferencesKey("language_code")
        val KEY_TAGS = stringPreferencesKey("selected_tags")
    }
}
