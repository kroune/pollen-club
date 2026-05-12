package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapPolygonDomain

interface MapRepository {
    suspend fun getPins(userId: Long): ApiResult<List<MapPinDomain>>
    suspend fun getPolygonForecast(pollenId: Int): ApiResult<List<MapPolygonDomain>>
    suspend fun getHashTags(): ApiResult<List<HashTagDomain>>
}
