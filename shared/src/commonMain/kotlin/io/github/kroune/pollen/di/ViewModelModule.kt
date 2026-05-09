package io.github.kroune.pollen.di

import io.github.kroune.pollen.presentation.diary.DiaryViewModel
import io.github.kroune.pollen.presentation.feed.FeedViewModel
import io.github.kroune.pollen.presentation.home.ForecastDetailViewModel
import io.github.kroune.pollen.presentation.home.HomeViewModel
import io.github.kroune.pollen.presentation.map.MapViewModel
import io.github.kroune.pollen.presentation.medications.MedicationsViewModel
import io.github.kroune.pollen.presentation.phenology.PhenologyViewModel
import io.github.kroune.pollen.presentation.reference.ReferenceViewModel
import io.github.kroune.pollen.presentation.sensitivity.SensitivityViewModel
import io.github.kroune.pollen.presentation.settings.SettingsLanguageViewModel
import io.github.kroune.pollen.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::DiaryViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::MedicationsViewModel)
    viewModelOf(::PhenologyViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SettingsLanguageViewModel)
    viewModelOf(::SensitivityViewModel)
    viewModelOf(::ReferenceViewModel)
    factory { params -> ForecastDetailViewModel(params.get(), get(), get(), get()) }
}
