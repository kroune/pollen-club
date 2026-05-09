package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocaleProviderImpl(
    private val prefs: AppPreferences,
) : LocaleProvider {

    override val currentLocale: Flow<AppLocale> = prefs.languageCode.map { it.toAppLocale() }

    override suspend fun current(): AppLocale = prefs.languageCode.first().toAppLocale()
}

private fun String.toAppLocale(): AppLocale =
    if (this == "en") AppLocale.EN else AppLocale.RU
