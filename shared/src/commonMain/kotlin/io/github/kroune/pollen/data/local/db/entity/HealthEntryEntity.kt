package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_entries")
data class HealthEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long = 0,
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "feeling") val feeling: Int = 0,
    @ColumnInfo(name = "eyes") val eyes: Int = 0,
    @ColumnInfo(name = "nose") val nose: Int = 0,
    @ColumnInfo(name = "throat") val throat: Int = 0,
    @ColumnInfo(name = "lungs") val lungs: Int = 0,
    @ColumnInfo(name = "general") val general: Int = 0,
    @ColumnInfo(name = "other") val other: String = "",
    @ColumnInfo(name = "time") val time: Long = 0,
    @ColumnInfo(name = "location_name") val locationName: String = "",
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "tags") val tags: String = "",
    @ColumnInfo(name = "latitude") val latitude: Double = 0.0,
    @ColumnInfo(name = "longitude") val longitude: Double = 0.0,
    @ColumnInfo(name = "location_id") val locationId: Int = 0,
    @ColumnInfo(name = "default_pollen") val defaultPollen: Int = 0,
    @ColumnInfo(name = "symptom_tags") val symptomTags: String = "",
)
