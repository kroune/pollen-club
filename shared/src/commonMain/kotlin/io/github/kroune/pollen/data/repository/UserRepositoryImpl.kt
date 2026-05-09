package io.github.kroune.pollen.data.repository

import io.github.kroune.pollen.data.local.db.dao.UserDao
import io.github.kroune.pollen.data.local.db.entity.UserEntity
import io.github.kroune.pollen.data.remote.api.PollenApiService
import io.github.kroune.pollen.data.remote.dto.request.SetUserRequest
import io.github.kroune.pollen.domain.model.ApiResult
import io.github.kroune.pollen.domain.model.UserDomain
import io.github.kroune.pollen.domain.model.safeApiCall
import io.github.kroune.pollen.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val api: PollenApiService,
    private val userDao: UserDao,
) : UserRepository {

    override suspend fun registerOrUpdateUser(user: UserDomain): ApiResult<Long> = safeApiCall {
        val response = api.setUser(
            SetUserRequest(
                id = user.serverId,
                name = user.name,
                lastName = user.lastName,
                location = user.location,
                ages = user.age,
                activity = user.activity,
            ),
        )
        val serverId = response.userId
        val existing = userDao.getUser()
        if (existing != null) {
            userDao.upsert(
                existing.copy(
                    name = user.name,
                    lastName = user.lastName,
                    location = user.location,
                    age = user.age,
                    activity = user.activity,
                    serverId = serverId,
                ),
            )
        } else {
            userDao.upsert(
                UserEntity(
                    name = user.name,
                    lastName = user.lastName,
                    location = user.location,
                    age = user.age,
                    activity = user.activity,
                    serverId = serverId,
                ),
            )
        }
        serverId
    }

    override suspend fun getLocalUser(): UserDomain? {
        return userDao.getUser()?.toDomain()
    }

    override fun observeUser(): Flow<UserDomain?> {
        return userDao.observeUser().map { it?.toDomain() }
    }

    override suspend fun updateAllergens(allergenIds: List<Int>) {
        val user = userDao.getUser() ?: return
        userDao.updateAllergens(user.id, allergenIds.joinToString(","))
    }

    private fun UserEntity.toDomain(): UserDomain = UserDomain(
        id = id,
        name = name,
        lastName = lastName,
        location = location,
        age = age,
        activity = activity,
        serverId = serverId,
        selectedAllergens = if (selectedAllergens.isBlank()) emptyList()
        else selectedAllergens.split(",").mapNotNull { it.trim().toIntOrNull() },
    )
}
