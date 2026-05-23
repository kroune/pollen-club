package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val prefs: AppPreferences,
    localeProvider: LocaleProvider,
) : SettingsRepository {

    override val locale: Flow<AppLocale> = localeProvider.currentLocale

    override val acceptedTerms: Flow<Boolean> = prefs.acceptedTerms

    override val isFirstRun: Flow<Boolean> = prefs.isFirstRun

    override suspend fun setLocale(locale: AppLocale) {
        prefs.setLanguageCode(locale.tag)
    }

    override suspend fun setAcceptedTerms(accepted: Boolean) {
        prefs.setAcceptedTerms(accepted)
    }

    override suspend fun setFirstRun(firstRun: Boolean) {
        prefs.setFirstRun(firstRun)
    }
}
