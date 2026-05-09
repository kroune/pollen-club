package io.github.kroune.pollen.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ExpertInfoDomain(
    val name: String,
    val title: String,
)

data class CommentDomain(
    val id: Int,
    val date: String,
    val expertId: Int,
    val expert: ExpertInfoDomain,
    val text: String,
    val locationId: Int,
    val pinned: Boolean,
)

data class VkPostDomain(
    val id: Int,
    val date: String,
    val location: String,
    val content: String,
)

data class MediaItemDomain(
    val id: Int,
    val date: String,
    val type: MediaType,
    val url: String,
    val description: String,
)

enum class MediaType { IMAGE, VIDEO }

data class FriendFeelDomain(
    val id: Int,
    val location: String,
    val date: String,
    val friendId: Int,
    val feeling: Feeling,
)

data class FeedDataDomain(
    val comments: ImmutableList<CommentDomain> = persistentListOf(),
    val vkPosts: ImmutableList<VkPostDomain> = persistentListOf(),
    val media: ImmutableList<MediaItemDomain> = persistentListOf(),
    val friendFeels: ImmutableList<FriendFeelDomain> = persistentListOf(),
)
