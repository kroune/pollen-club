package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kroune.pollen.data.local.db.entity.PhenologyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhenologyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(observation: PhenologyEntity): Long

    @Query("SELECT * FROM phenology ORDER BY date DESC, time DESC")
    fun observeAll(): Flow<List<PhenologyEntity>>
}
