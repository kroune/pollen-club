package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.mapper.toDomain
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.GetUserRequest
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.LocaleProvider
import io.github.kroune.pollen.domain.repository.FeedRepository
import kotlinx.collections.immutable.toImmutableList

class FeedRepositoryImpl(
    private val api: PollenApiService,
    private val localeProvider: LocaleProvider,
) : FeedRepository {

    override suspend fun getFeed(userId: Long): FeedDataDomain {
        val locale = localeProvider.current()
        val response = api.getCommentsAndLenta(GetUserRequest(userId))
        return FeedDataDomain(
            comments = response.comments.map { it.toDomain(locale) }.toImmutableList(),
            vkPosts = response.lentaVk.map { it.toDomain() }.toImmutableList(),
            media = response.lentaMedia.map { it.toDomain(locale) }.toImmutableList(),
            friendFeels = response.friends.map { it.toDomain() }.toImmutableList(),
        )
    }
}
