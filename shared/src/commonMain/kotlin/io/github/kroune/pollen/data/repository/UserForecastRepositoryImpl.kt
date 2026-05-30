package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.UserForecastDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.UserForecastRepository
import io.github.kroune.pollen.domain.session.UserSession

class UserForecastRepositoryImpl(
    private val api: PollenApiService,
    private val localeProvider: LocaleProvider,
    private val session: UserSession,
) : UserForecastRepository {

    override suspend fun getUserForecast(): ApiResult<UserForecastDomain> = safeApiCall {
        val locale = localeProvider.current()
        val response = api.getUserForecast(GetUserRequest(session.requireUserId()))
        val result = response.result
            ?: return@safeApiCall UserForecastDomain(info = emptyList(), forecasts = emptyList())
        UserForecastDomain(
            info = result.userInfo.map { it.toDomain(locale) },
            forecasts = result.userForecast.map { it.toDomain() },
        )
    }
}
