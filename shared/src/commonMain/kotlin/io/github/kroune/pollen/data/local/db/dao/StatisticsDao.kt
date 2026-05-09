package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.StatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Upsert
    suspend fun upsertAll(stats: List<StatisticsEntity>)

    @Query("SELECT * FROM statistics WHERE location_id = :locationId ORDER BY date DESC")
    fun observeByLocation(locationId: Int): Flow<List<StatisticsEntity>>

    @Query("SELECT MAX(id) FROM statistics")
    suspend fun getMaxId(): Int?
}
