package io.github.kroune.pollen.data.remote.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    @SerialName("current_weather") val currentWeather: CurrentWeatherDto? = null,
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature") val temperature: Double = 0.0,
    @SerialName("weathercode") val weatherCode: Int = 0,
    @SerialName("is_day") val isDay: Int = 1,
)

interface WeatherApiService {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): OpenMeteoResponse
}

class WeatherApiServiceImpl(
    private val client: HttpClient,
) : WeatherApiService {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): OpenMeteoResponse {
        return client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("current_weather", true)
        }.body()
    }
}
