package io.github.kroune.pollen.domain.model

import kotlinx.datetime.LocalDate

data class ExpertInfoDomain(
    val name: String,
    val title: String,
)

data class CommentDomain(
    val id: Int,
    val date: LocalDate,
    val expertId: Int,
    val expert: ExpertInfoDomain,
    val text: String,
    val locationId: Int,
    val pinned: Boolean,
)

data class VkPostDomain(
    val id: Int,
    val date: LocalDate,
    val location: String,
    val userName: String,
    val content: String,
)

data class MediaItemDomain(
    val id: Int,
    val date: LocalDate,
    val type: MediaType,
    val url: String,
    val description: String,
)

enum class MediaType { IMAGE, VIDEO }

data class FriendFeelDomain(
    val id: Int,
    val location: String,
    val date: LocalDate,
    val friendId: Int,
    val feeling: Feeling,
)

data class FeedDataDomain(
    val comments: List<CommentDomain> = emptyList(),
    val vkPosts: List<VkPostDomain> = emptyList(),
    val media: List<MediaItemDomain> = emptyList(),
    val friendFeels: List<FriendFeelDomain> = emptyList(),
)
