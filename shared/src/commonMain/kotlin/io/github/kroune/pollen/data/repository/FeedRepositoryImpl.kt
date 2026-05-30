package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.FeedRepository
import io.github.kroune.pollen.domain.session.UserSession

class FeedRepositoryImpl(
    private val api: PollenApiService,
    private val localeProvider: LocaleProvider,
    private val session: UserSession,
) : FeedRepository {

    override suspend fun getFeed(): FeedDataDomain {
        val locale = localeProvider.current()
        val response = api.getCommentsAndLenta(GetUserRequest(session.requireUserId()))
        return FeedDataDomain(
            comments = response.comments.map { it.toDomain(locale) },
            vkPosts = response.lentaVk.map { it.toDomain() },
            media = response.lentaMedia.map { it.toDomain(locale) },
            friendFeels = response.friends.map { it.toDomain() },
        )
    }
}
