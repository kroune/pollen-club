package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kroune.pollen.data.local.db.entity.HealthEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HealthEntryEntity): Long

    @Query("SELECT * FROM health_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): HealthEntryEntity?

    @Query("SELECT * FROM health_entries WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun observeByDateRange(from: String, to: String): Flow<List<HealthEntryEntity>>

    @Query("SELECT * FROM health_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<HealthEntryEntity>>

    @Query("SELECT * FROM health_entries WHERE is_synced = 0")
    suspend fun getUnsynced(): List<HealthEntryEntity>

    @Query("UPDATE health_entries SET is_synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)
}
