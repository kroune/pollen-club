package io.github.kroune.pollen.di

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
}
