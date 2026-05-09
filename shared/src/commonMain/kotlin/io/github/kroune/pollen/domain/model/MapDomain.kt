package io.github.kroune.pollen.domain.model

data class MapPinDomain(
    val date: String,
    val feeling: Feeling,
    val latitude: Double,
    val longitude: Double,
    val pollenType: Int,
    val tags: String,
    val friendId: Int,
)

data class GeoPoint(val latitude: Double, val longitude: Double)

typealias GeoRing = List<GeoPoint>

typealias GeoPolygon = List<GeoRing>

data class MapPolygonDomain(
    val polygons: List<GeoPolygon>,
    val color: String,
    val opacity: Float,
    val fillColor: String,
    val fillOpacity: Float,
)
