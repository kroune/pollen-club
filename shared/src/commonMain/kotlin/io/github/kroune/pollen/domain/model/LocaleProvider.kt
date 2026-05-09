package io.github.kroune.pollen.domain.model

import kotlinx.coroutines.flow.Flow

interface LocaleProvider {
    val currentLocale: Flow<AppLocale>
    suspend fun current(): AppLocale
}
