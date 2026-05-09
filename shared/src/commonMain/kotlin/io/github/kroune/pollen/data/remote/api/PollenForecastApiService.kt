package io.github.kroune.pollen.data.remote.api

import io.github.kroune.pollen.data.remote.dto.response.PolygonDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface PollenForecastApiService {
    suspend fun getPolygonForecast(allergen: String, dateTime: String): List<PolygonDataResponse>
}

class PollenForecastApiServiceImpl(
    private val client: HttpClient,
) : PollenForecastApiService {

    private companion object {
        const val BASE_URL = "https://api.pollen.club/static/forecasts"
    }

    override suspend fun getPolygonForecast(
        allergen: String,
        dateTime: String,
    ): List<PolygonDataResponse> {
        return client.get("$BASE_URL/$allergen/$dateTime.json").body()
    }
}
