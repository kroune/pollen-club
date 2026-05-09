package io.github.kroune.pollen.domain.model

data class LocationDomain(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
)
