package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kroune.pollen.data.local.db.entity.TherapyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TherapyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(therapy: TherapyEntity): Long

    @Query("SELECT * FROM therapies ORDER BY start_date DESC")
    fun observeAll(): Flow<List<TherapyEntity>>

    @Query("SELECT * FROM therapies WHERE date = :date")
    suspend fun getByDate(date: LocalDate): List<TherapyEntity>

    @Delete
    suspend fun delete(therapy: TherapyEntity)
}
