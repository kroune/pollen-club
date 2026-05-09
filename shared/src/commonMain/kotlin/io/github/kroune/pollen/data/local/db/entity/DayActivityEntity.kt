package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_activities")
data class DayActivityEntity(
    @PrimaryKey val date: String,
    @ColumnInfo(name = "app_opened") val appOpened: Boolean = false,
    @ColumnInfo(name = "health_set") val healthSet: Boolean = false,
    @ColumnInfo(name = "symptoms_set") val symptomsSet: Boolean = false,
    @ColumnInfo(name = "map_opened") val mapOpened: Boolean = false,
    @ColumnInfo(name = "experts_read") val expertsRead: Boolean = false,
)
