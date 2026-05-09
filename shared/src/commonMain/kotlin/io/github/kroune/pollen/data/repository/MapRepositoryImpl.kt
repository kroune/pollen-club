package io.github.kroune.pollen.data.repository

import co.touchlab.kermit.Logger
import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.api.PollenForecastApiService
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.MapRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val logger = Logger.withTag("MapRepository")

class MapRepositoryImpl(
    private val api: PollenApiService,
    private val forecastApi: PollenForecastApiService,
) : MapRepository {

    override suspend fun getPins(userId: Long): ApiResult<List<MapPinDomain>> =
        safeApiCall(logger, "get pins for user=$userId") {
            val response = api.getPinsWithFriends(GetUserRequest(userId))
            logger.i { "Loaded ${response.pins.size} pins" }
            response.pins.map { it.toDomain() }
        }

    override suspend fun getPolygonForecast(
        pollenName: String,
    ): ApiResult<List<MapPolygonDomain>> {
        val allergen = pollenName.replace('ё', 'е').replace('Ё', 'Е')
        if (allergen !in FORECAST_ALLERGENS) {
            return ApiResult.Success(emptyList())
        }
        val now = kotlin.time.Clock.System.now()
        val local = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val dateTime = "${local.year}/${local.monthNumber}/${local.dayOfMonth}/${local.hour}"
        return safeApiCall(logger, "get polygon forecast allergen=$allergen dateTime=$dateTime") {
            val polygons = forecastApi.getPolygonForecast(allergen, dateTime)
            logger.i { "Loaded ${polygons.size} polygons for $allergen" }
            polygons.map { it.toDomain() }
        }
    }

    companion object {
        // Server only has polygon forecasts for these allergens (from decompiled MyMapFragment.getAllergen)
        private val FORECAST_ALLERGENS = setOf("Береза", "Злаки", "Амброзия", "Олива", "Ольха", "Полынь")
    }

    override suspend fun getHashTags(): ApiResult<List<HashTagDomain>> =
        safeApiCall(logger, "get hashtags") {
            api.getHashTags().hashtags.map { it.toDomain() }
        }
}
