package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.DeviceCoordinates
import io.github.kroune.pollen.domain.model.LocationAvailability
import kotlinx.coroutines.flow.StateFlow

interface DeviceLocationProvider {
    val availability: StateFlow<LocationAvailability>
    suspend fun getCurrentLocation(): DeviceCoordinates?
    fun refreshAvailability()
}
