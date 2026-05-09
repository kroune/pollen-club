package io.github.kroune.pollen.presentation.map

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MapPinUi(
    val latitude: Double,
    val longitude: Double,
    val severity: Int,
    val label: String,
)

@Immutable
data class MapFilterUi(
    val id: String,
    val name: String,
    val isActive: Boolean,
)

@Immutable
data class MapAllergenChipUi(
    val pollenId: Int,
    val name: String,
)

@Immutable
data class MapScreenDataUi(
    val pins: ImmutableList<MapPinUi>,
    val filters: ImmutableList<MapFilterUi>,
    val allergenChips: ImmutableList<MapAllergenChipUi>,
    val selectedAllergenIndex: Int,
)
