package io.github.kroune.pollen.di

import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.util.applyMokoLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.mp.KoinPlatform

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
    val koin = KoinPlatform.getKoin()
    val localeProvider: LocaleProvider = koin.get()
    val appScope: CoroutineScope = koin.get()
    appScope.launch {
        localeProvider.currentLocale.collect { applyMokoLocale(it) }
    }

    // Warm up identity at startup: register with the server on first launch so a user id is ready
    // before any screen needs it. Best-effort — requireUserId() registers on demand if this fails.
    val userSession: UserSession = koin.get()
    appScope.launch { userSession.bootstrap() }
}
