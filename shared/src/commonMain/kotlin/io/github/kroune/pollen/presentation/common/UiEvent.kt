package io.github.kroune.pollen.presentation.common

import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc

sealed interface UiEvent {
    data class ShowError(val message: StringDesc) : UiEvent {
        /** Convenience constructor for callers still passing raw strings. */
        constructor(raw: String) : this(StringDesc.Raw(raw))
    }
}
