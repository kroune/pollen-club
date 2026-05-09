package io.github.kroune.pollen.presentation.common

sealed interface UiEvent {
    data class ShowError(val message: String) : UiEvent
}
