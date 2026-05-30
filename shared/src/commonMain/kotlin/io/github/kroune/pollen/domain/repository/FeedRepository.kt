package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.FeedDataDomain

interface FeedRepository {
    suspend fun getFeed(): FeedDataDomain
}
