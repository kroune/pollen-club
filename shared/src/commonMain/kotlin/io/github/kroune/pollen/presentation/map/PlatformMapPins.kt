package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Stable
import io.github.kroune.pollen.domain.model.MapPinDomain

@Stable
expect class PlatformMapPins

expect fun List<MapPinDomain>.toPlatformMapPins(): PlatformMapPins
