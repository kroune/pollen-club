package io.github.kroune.pollen.domain.model

data class FriendDomain(
    val id: Int,
    val friendId: Int,
    val name: String = "",
)

data class FriendLastPinDomain(
    val friendId: Int,
    val feeling: Feeling,
    val pollenType: Int,
    val date: String,
)
