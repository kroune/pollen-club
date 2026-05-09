package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.AppLocale
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val locale: Flow<AppLocale>
    val acceptedTerms: Flow<Boolean>
    val isFirstRun: Flow<Boolean>
    suspend fun setLocale(locale: AppLocale)
    suspend fun setAcceptedTerms(accepted: Boolean)
    suspend fun setFirstRun(firstRun: Boolean)
}
