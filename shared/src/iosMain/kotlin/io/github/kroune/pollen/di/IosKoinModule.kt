package io.github.kroune.pollen.di

import io.github.kroune.pollen.data.repository.IosDeviceLocationProvider
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val iosPlatformModule: Module = module {
    single<DeviceLocationProvider> { IosDeviceLocationProvider() }
}

fun initKoinIos() {
    initKoin(listOf(iosPlatformModule))
}
