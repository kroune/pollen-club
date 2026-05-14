package io.github.kroune.pollen.domain.model

data class DeviceCoordinates(val latitude: Double, val longitude: Double)

sealed interface LocationAvailability {
    data object Unknown : LocationAvailability
    data object Available : LocationAvailability
    data object PermissionDenied : LocationAvailability
    data object LocationDisabled : LocationAvailability
}

data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val regionId: Int,
    val regionName: String,
)

const val DEFAULT_CENTER_LATITUDE = 55.7558
const val DEFAULT_CENTER_LONGITUDE = 37.6173
