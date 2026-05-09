package io.github.kroune.pollen.domain.repository

import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.UserDomain
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerOrUpdateUser(user: UserDomain): ApiResult<Long>
    suspend fun getLocalUser(): UserDomain?
    fun observeUser(): Flow<UserDomain?>
    suspend fun updateAllergens(allergenIds: List<Int>)
}
