package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.HashTagDomain
import io.github.kroune.pollen.domain.model.MapPinDomain
import io.github.kroune.pollen.domain.model.MapRingDomain

interface MapRepository {
    suspend fun getPins(): ApiResult<List<MapPinDomain>>
    suspend fun getPolygonForecast(pollenId: Int): ApiResult<List<MapRingDomain>>
    suspend fun getHashTags(): ApiResult<List<HashTagDomain>>
}
