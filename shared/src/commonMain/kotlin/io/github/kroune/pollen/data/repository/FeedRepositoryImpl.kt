package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.domain.session.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FeedRepositoryImpl(
    private val api: PollenApiService,
    private val localeProvider: LocaleProvider,
    private val session: UserSession,
) : FeedRepository {

    override fun getFeed(): Flow<ApiResult<FeedDataDomain>> = flow {
        val fetch = safeApiCall { api.getCommentsAndLenta(GetUserRequest(session.requireUserId())) }
        when (fetch) {
            is ApiResult.Error -> emit(fetch)
            is ApiResult.Success -> {
                val response = fetch.data
                // On success, re-map the cached response on every locale change. On failure we
                // emit a single Error above and never observe locale, so a later locale change
                // produces no emission.
                emitAll(
                    localeProvider.currentLocale.map { locale ->
                        ApiResult.Success(
                            FeedDataDomain(
                                comments = response.comments.map { it.toDomain(locale) },
                                vkPosts = response.lentaVk.map { it.toDomain() },
                                media = response.lentaMedia.map { it.toDomain(locale) },
                                friendFeels = response.friends.map { it.toDomain() },
                            ),
                        )
                    },
                )
            }
        }
    }
}
