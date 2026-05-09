package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Upsert
    suspend fun upsertAll(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations ORDER BY id")
    fun observeAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations ORDER BY id")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getById(id: Int): LocationEntity?
}
