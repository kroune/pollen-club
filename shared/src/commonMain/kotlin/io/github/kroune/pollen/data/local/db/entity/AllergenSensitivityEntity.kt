package io.github.kroune.pollen.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allergen_sensitivity")
data class AllergenSensitivityEntity(
    @PrimaryKey
    @ColumnInfo(name = "pollen_id") val pollenId: Int,
    @ColumnInfo(name = "sensitivity_level") val sensitivityLevel: Int = 0,
)
