package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.WeatherDomain

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): ApiResult<WeatherDomain>
}
