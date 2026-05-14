package io.github.kroune.pollen.domain.usecase

import io.github.kroune.pollen.domain.model.ResolvedLocation
import io.github.kroune.pollen.domain.repository.DeviceLocationProvider
import io.github.kroune.pollen.domain.repository.LocationRepository
import io.github.kroune.pollen.domain.repository.UserRepository

class CoordinateResolver(
    private val deviceLocationProvider: DeviceLocationProvider,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
) {
    suspend fun resolve(): ResolvedLocation? {
        val user = userRepository.getLocalUser()
        val regionId = user?.location ?: 0
        val region = if (regionId > 0) locationRepository.getById(regionId) else null

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
