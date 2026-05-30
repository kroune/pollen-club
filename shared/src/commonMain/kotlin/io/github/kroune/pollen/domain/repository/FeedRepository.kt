package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.FeedDataDomain
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getFeed(): Flow<ApiResult<FeedDataDomain>>
}
