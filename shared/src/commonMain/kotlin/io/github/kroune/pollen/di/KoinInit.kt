package io.github.kroune.pollen.di

import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.util.applyMokoLocale
import io.github.kroune.pollen.domain.model.AppLocale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(platformModules: List<Module> = emptyList()) {
    startKoin {
        modules(
            networkModule,
            databaseModule,
            sharedModule,
            viewModelModule,
            *platformModules.toTypedArray(),
        )
    }
    val prefs = org.koin.core.context.GlobalContext.get().get<AppPreferences>()
    val languageCode = runBlocking { prefs.languageCode.first() }
    val locale = if (languageCode == "en") AppLocale.EN else AppLocale.RU
    applyMokoLocale(locale)
}
