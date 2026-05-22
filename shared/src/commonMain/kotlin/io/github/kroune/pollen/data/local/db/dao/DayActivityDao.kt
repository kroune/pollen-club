package io.github.kroune.pollen.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.github.kroune.pollen.data.local.db.entity.DayActivityEntity
import kotlinx.datetime.LocalDate

@Dao
interface DayActivityDao {
    @Upsert
    suspend fun upsert(activity: DayActivityEntity)

    @Query("SELECT * FROM day_activities WHERE date = :date")
    suspend fun getByDate(date: LocalDate): DayActivityEntity?
}
