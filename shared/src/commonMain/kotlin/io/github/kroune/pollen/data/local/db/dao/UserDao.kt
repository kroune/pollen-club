package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("UPDATE users SET server_id = :serverId WHERE id = :id")
    suspend fun updateServerId(id: Long, serverId: Long)

    @Query("UPDATE users SET selected_allergens = :allergens WHERE id = :id")
    suspend fun updateAllergens(id: Long, allergens: String)
}
