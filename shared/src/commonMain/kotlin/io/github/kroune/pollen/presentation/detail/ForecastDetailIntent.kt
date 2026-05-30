package io.github.kroune.pollen.presentation.detail

sealed interface ForecastDetailIntent {
    data object ReloadData : ForecastDetailIntent
    data object ToggleFeelingLine : ForecastDetailIntent
}