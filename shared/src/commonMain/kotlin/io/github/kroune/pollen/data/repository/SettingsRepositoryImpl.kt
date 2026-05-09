package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val prefs: AppPreferences,
) : SettingsRepository {

    override val locale: Flow<AppLocale> = prefs.languageCode.map { code ->
        if (code == "en") AppLocale.EN else AppLocale.RU
    }

    override val acceptedTerms: Flow<Boolean> = prefs.acceptedTerms

    override val isFirstRun: Flow<Boolean> = prefs.isFirstRun

    override suspend fun setLocale(locale: AppLocale) {
        prefs.setLanguageCode(if (locale == AppLocale.EN) "en" else "ru")
    }

    override suspend fun setAcceptedTerms(accepted: Boolean) {
        prefs.setAcceptedTerms(accepted)
    }

    override suspend fun setFirstRun(firstRun: Boolean) {
        prefs.setFirstRun(firstRun)
    }
}
