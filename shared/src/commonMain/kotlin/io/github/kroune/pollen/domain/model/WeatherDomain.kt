package io.github.kroune.pollen.domain.model

data class WeatherDomain(
    val temperature: Double,
    val weatherCode: Int,
    val isDay: Boolean,
)
