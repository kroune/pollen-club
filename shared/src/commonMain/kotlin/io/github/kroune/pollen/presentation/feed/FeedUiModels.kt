package io.github.kroune.pollen.presentation.feed

import androidx.compose.runtime.Immutable
import io.github.kroune.pollen.domain.model.Feeling
import io.github.kroune.pollen.domain.model.MediaType

enum class FeedFilter { ALL, EXPERTS, SOCIAL, MEDIA, FRIENDS }

@Immutable
sealed interface FeedItemUi {
    val id: String
    val date: String

    @Immutable
    data class ExpertComment(
        override val id: String,
        override val date: String,
        val expertName: String,
        val expertTitle: String,
        val text: String,
        val pinned: Boolean,
    ) : FeedItemUi

    @Immutable
    data class SocialPost(
        override val id: String,
        override val date: String,
        val location: String,
        val content: String,
    ) : FeedItemUi

    @Immutable
    data class MediaContent(
        override val id: String,
        override val date: String,
        val type: MediaType,
        val url: String,
        val description: String,
    ) : FeedItemUi

    @Immutable
    data class FriendStatus(
        override val id: String,
        override val date: String,
        val friendId: Int,
        val location: String,
        val feeling: Feeling,
    ) : FeedItemUi
}
