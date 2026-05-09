package io.github.kroune.pollen.domain.model

data class UserDomain(
    val id: Long = 0,
    val name: String = "",
    val lastName: String = "",
    val location: Int = 0,
    val age: Int = 0,
    val activity: Int = 0,
    val serverId: Long = 0,
    val selectedAllergens: List<Int> = emptyList(),
)
