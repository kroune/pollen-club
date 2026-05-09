package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.UserForecastDomain

interface UserForecastRepository {
    suspend fun getUserForecast(userId: Long): ApiResult<UserForecastDomain>
}
