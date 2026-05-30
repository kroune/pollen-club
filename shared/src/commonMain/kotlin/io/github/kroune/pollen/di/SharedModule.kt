package io.github.kroune.pollen.di

import io.github.kroune.pollen.data.repository.TodayProviderImpl
import io.github.kroune.pollen.data.repository.FeedRepositoryImpl
import io.github.kroune.pollen.domain.usecase.CoordinateResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import io.github.kroune.pollen.data.repository.FriendsRepositoryImpl
import io.github.kroune.pollen.data.repository.HealthRepositoryImpl
import io.github.kroune.pollen.data.repository.LocaleProviderImpl
import io.github.kroune.pollen.data.repository.LocationRepositoryImpl
import io.github.kroune.pollen.data.repository.MapRepositoryImpl
import io.github.kroune.pollen.data.repository.MedicationRepositoryImpl
import io.github.kroune.pollen.data.repository.PhenologyRepositoryImpl
import io.github.kroune.pollen.data.repository.PollenRepositoryImpl
import io.github.kroune.pollen.data.repository.SettingsRepositoryImpl
import io.github.kroune.pollen.data.repository.StatisticsRepositoryImpl
import io.github.kroune.pollen.data.repository.UserForecastRepositoryImpl
import io.github.kroune.pollen.data.session.UserSessionImpl
import io.github.kroune.pollen.data.repository.PersonalIndexRepositoryImpl
import io.github.kroune.pollen.data.repository.SensitivityRepositoryImpl
import io.github.kroune.pollen.data.repository.WeatherRepositoryImpl
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.TodayProvider
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.domain.repository.FriendsRepository
import io.github.kroune.pollen.domain.repository.HealthRepository
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.MapRepository
import io.github.kroune.pollen.domain.repository.MedicationRepository
import io.github.kroune.pollen.domain.repository.PhenologyRepository
import io.github.kroune.pollen.domain.repository.PollenRepository
import io.github.kroune.pollen.domain.repository.SettingsRepository
import io.github.kroune.pollen.domain.repository.StatisticsRepository
import io.github.kroune.pollen.domain.repository.UserForecastRepository
import io.github.kroune.pollen.domain.session.UserSession
import io.github.kroune.pollen.domain.repository.PersonalIndexRepository
import io.github.kroune.pollen.domain.repository.SensitivityRepository
import io.github.kroune.pollen.domain.repository.WeatherRepository
import org.koin.dsl.module

val sharedModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single<LocaleProvider> { LocaleProviderImpl(get()) }
    single<TodayProvider> { TodayProviderImpl(get<CoroutineScope>()) }

    single<UserSession> { UserSessionImpl(get(), get(), get()) }
    single<PollenRepository> { PollenRepositoryImpl(get(), get(), get(), get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get(), get()) }
    single<HealthRepository> { HealthRepositoryImpl(get(), get(), get()) }
    single<StatisticsRepository> { StatisticsRepositoryImpl(get(), get(), get()) }
    single<FeedRepository> { FeedRepositoryImpl(get(), get(), get()) }
    single<MapRepository> { MapRepositoryImpl(get(), get(), get(), get()) }
    single<FriendsRepository> { FriendsRepositoryImpl(get(), get(), get()) }
    single<MedicationRepository> { MedicationRepositoryImpl(get(), get(), get(), get(), get()) }
    single<PhenologyRepository> { PhenologyRepositoryImpl(get(), get(), get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }
    single<UserForecastRepository> { UserForecastRepositoryImpl(get(), get(), get()) }
    single<SensitivityRepository> { SensitivityRepositoryImpl(get()) }
    single<PersonalIndexRepository> { PersonalIndexRepositoryImpl(get()) }
    factory { CoordinateResolver(get(), get(), get()) }
}
