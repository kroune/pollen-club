package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import io.github.kroune.pollen.domain.model.MapPinDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Stable
actual class PlatformMapPins(val items: ImmutableList<MapPinDomain>)

actual fun List<MapPinDomain>.toPlatformMapPins(): PlatformMapPins =
    PlatformMapPins(toImmutableList())
