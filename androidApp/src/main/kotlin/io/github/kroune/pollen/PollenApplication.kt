package io.github.kroune.pollen

import android.app.Application
import android.content.Context
import io.github.kroune.pollen.data.repository.AndroidDeviceLocationProvider
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import io.github.kroune.pollen.di.initKoin
import org.koin.dsl.module

class PollenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(listOf(module {
            single<Context> { this@PollenApplication }
            single<DeviceLocationProvider> { AndroidDeviceLocationProvider(get()) }
        }))
    }
}
