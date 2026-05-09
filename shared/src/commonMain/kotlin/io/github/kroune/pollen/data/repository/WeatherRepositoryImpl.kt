package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.weather.WeatherApiService
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.WeatherDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApiService,
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): ApiResult<WeatherDomain> =
        safeApiCall {
            val response = weatherApi.getCurrentWeather(latitude, longitude)
            val currentWeather = response.currentWeather
                ?: error("No current weather data in response")
            currentWeather.toDomain()
        }
}
