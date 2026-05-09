package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medication_intakes",
    indices = [Index(value = ["therapy_id", "date"], unique = true)],
)
data class MedicationIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "therapy_id") val therapyId: Long,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "taken") val taken: Boolean = false,
)
