package io.github.kroune.pollen.presentation.detail.viewmodel

sealed interface ForecastDetailIntent {
    data object ReloadData : ForecastDetailIntent
    data object ToggleFeelingLine : ForecastDetailIntent
}