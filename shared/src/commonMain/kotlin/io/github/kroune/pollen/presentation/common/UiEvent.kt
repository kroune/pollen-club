package io.github.kroune.pollen.presentation.common

import dev.icerock.moko.resources.desc.StringDesc

sealed interface UiEvent {
    data class ShowError(val message: StringDesc) : UiEvent
}
