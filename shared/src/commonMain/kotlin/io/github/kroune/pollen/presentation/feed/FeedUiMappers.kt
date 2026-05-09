package io.github.kroune.pollen.presentation.feed

import io.github.kroune.pollen.domain.model.AppLocale
import io.github.kroune.pollen.domain.model.ExpertRegistry
import io.github.kroune.pollen.domain.model.FeedDataDomain
import io.github.kroune.pollen.domain.model.MediaType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun FeedDataDomain.toUnifiedFeedItems(locale: AppLocale): ImmutableList<FeedItemUi> {
    val items = mutableListOf<FeedItemUi>()

    comments.forEach { c ->
        val expert = ExpertRegistry.get(c.expertId, locale)
        items.add(
            FeedItemUi.ExpertComment(
                id = "comment_${c.id}",
                date = c.date,
                expertName = expert.name.name,
                expertTitle = expert.name.title,
                text = c.text,
                pinned = c.pinned,
            ),
        )
    }

    vkPosts.forEach { p ->
        items.add(
            FeedItemUi.SocialPost(
                id = "vk_${p.id}",
                date = p.date,
                location = p.location,
                content = p.content,
            ),
        )
    }

    media.forEach { m ->
        items.add(
            FeedItemUi.MediaContent(
                id = "media_${m.id}",
                date = m.date,
                type = m.type,
                url = m.url,
                description = m.description,
            ),
        )
    }

    friendFeels.forEach { f ->
        items.add(
            FeedItemUi.FriendStatus(
                id = "friend_${f.id}",
                date = f.date,
                friendId = f.friendId,
                location = f.location,
                feeling = f.feeling,
            ),
        )
    }

    return items.sortedByDescending { it.date }.toImmutableList()
}

fun List<FeedItemUi>.filterBy(filter: FeedFilter): ImmutableList<FeedItemUi> = when (filter) {
    FeedFilter.ALL -> this.toImmutableList()
    FeedFilter.EXPERTS -> filterIsInstance<FeedItemUi.ExpertComment>().toImmutableList()
    FeedFilter.SOCIAL -> filterIsInstance<FeedItemUi.SocialPost>().toImmutableList()
    FeedFilter.MEDIA -> filterIsInstance<FeedItemUi.MediaContent>().toImmutableList()
    FeedFilter.FRIENDS -> filterIsInstance<FeedItemUi.FriendStatus>().toImmutableList()
}
