package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.domain.model.DeviceCoordinates
import io.github.kroune.pollen.domain.model.LocationAvailability
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosDeviceLocationProvider : DeviceLocationProvider {

    override val availability: StateFlow<LocationAvailability> =
        MutableStateFlow(LocationAvailability.Unknown).asStateFlow()

    override suspend fun getCurrentLocation(): DeviceCoordinates? = null

    override fun refreshAvailability() {}
}
