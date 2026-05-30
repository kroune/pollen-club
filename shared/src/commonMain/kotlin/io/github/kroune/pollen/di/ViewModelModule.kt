package io.github.kroune.pollen.di

import io.github.kroune.pollen.presentation.diary.DiaryViewModel
import io.github.kroune.pollen.presentation.feed.FeedViewModel
import io.github.kroune.pollen.presentation.friends.addfriend.AddFriendViewModel
import io.github.kroune.pollen.presentation.friends.list.FriendsListViewModel
import io.github.kroune.pollen.presentation.friends.myqr.MyQrViewModel
import io.github.kroune.pollen.presentation.detail.ForecastDetailViewModel
import io.github.kroune.pollen.presentation.home.HomeViewModel
import io.github.kroune.pollen.presentation.map.MapViewModel
import io.github.kroune.pollen.presentation.medications.MedicationsViewModel
import io.github.kroune.pollen.presentation.phenology.PhenologyViewModel
import io.github.kroune.pollen.presentation.reference.ReferenceViewModel
import io.github.kroune.pollen.presentation.sensitivity.SensitivityViewModel
import io.github.kroune.pollen.presentation.settings.language.SettingsLanguageViewModel
import io.github.kroune.pollen.presentation.settings.region.RegionSelectorViewModel
import io.github.kroune.pollen.presentation.settings.main.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::DiaryViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::FriendsListViewModel)
    viewModelOf(::AddFriendViewModel)
    viewModelOf(::MyQrViewModel)
    viewModelOf(::MedicationsViewModel)
    viewModelOf(::PhenologyViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SettingsLanguageViewModel)
    viewModelOf(::SensitivityViewModel)
    viewModelOf(::ReferenceViewModel)
    viewModelOf(::RegionSelectorViewModel)
    factory { params -> ForecastDetailViewModel(params.get(), get(), get(), get(), get(), get()) }
}
