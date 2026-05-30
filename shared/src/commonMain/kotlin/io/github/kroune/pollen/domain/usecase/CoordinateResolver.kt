package io.github.kroune.pollen.domain.usecase

import io.github.kroune.pollen.domain.model.ResolvedLocation
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.session.UserSession

class CoordinateResolver(
    private val deviceLocationProvider: DeviceLocationProvider,
    private val userSession: UserSession,
    private val locationRepository: LocationRepository,
) {
    suspend fun resolve(): ResolvedLocation? {
        val regionId = userSession.currentUser().location
        val region = regionId?.let { locationRepository.getById(it) }

        val gps = deviceLocationProvider.getCurrentLocation()

        val latitude: Double
        val longitude: Double
        if (gps != null) {
            latitude = gps.latitude
            longitude = gps.longitude
        } else if (region != null) {
            latitude = region.latitude
            longitude = region.longitude
        } else {
            return null
        }

        return ResolvedLocation(
            latitude = latitude,
            longitude = longitude,
            regionId = regionId,
            regionName = region?.name ?: "",
        )
    }
}
