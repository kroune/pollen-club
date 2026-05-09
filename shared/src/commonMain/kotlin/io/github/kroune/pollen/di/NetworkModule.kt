package io.github.kroune.pollen.di

import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.api.PollenApiServiceImpl
import io.github.kroune.pollen.data.remote.api.PollenForecastApiService
import io.github.kroune.pollen.data.remote.api.PollenForecastApiServiceImpl
import io.github.kroune.pollen.data.remote.api.createHttpClient
import io.github.kroune.pollen.data.remote.api.createPollenApiHttpClient
import io.github.kroune.pollen.data.remote.weather.WeatherApiService
import io.github.kroune.pollen.data.remote.weather.WeatherApiServiceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single(named("pollen")) { createHttpClient() }
    single(named("forecast")) { createPollenApiHttpClient() }
    single(named("weather")) { createHttpClient() }

    single<PollenApiService> { PollenApiServiceImpl(get(named("pollen"))) }
    single<PollenForecastApiService> { PollenForecastApiServiceImpl(get(named("forecast"))) }
    single<WeatherApiService> { WeatherApiServiceImpl(get(named("weather"))) }
}
