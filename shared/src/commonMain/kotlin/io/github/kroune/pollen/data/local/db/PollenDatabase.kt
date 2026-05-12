package io.github.kroune.pollen.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import io.github.kroune.pollen.data.local.db.dao.DayActivityDao
import io.github.kroune.pollen.data.local.db.dao.FriendDao
import io.github.kroune.pollen.data.local.db.dao.HealthDao
import io.github.kroune.pollen.data.local.db.dao.LevelDao
import io.github.kroune.pollen.data.local.db.dao.LocationDao
import io.github.kroune.pollen.data.local.db.dao.PhenologyDao
import io.github.kroune.pollen.data.local.db.dao.PollenDao
import io.github.kroune.pollen.data.local.db.dao.StatisticsDao
import io.github.kroune.pollen.data.local.db.dao.SyncStateDao
import io.github.kroune.pollen.data.local.db.dao.TherapyDao
import io.github.kroune.pollen.data.local.db.dao.UserDao
import io.github.kroune.pollen.data.local.db.dao.AllergenSensitivityDao
import io.github.kroune.pollen.data.local.db.dao.MedicationIntakeDao
import io.github.kroune.pollen.data.local.db.entity.AllergenSensitivityEntity
import io.github.kroune.pollen.data.local.db.entity.DayActivityEntity
import io.github.kroune.pollen.data.local.db.entity.ForecastLevelEntity
import io.github.kroune.pollen.data.local.db.entity.FriendEntity
import io.github.kroune.pollen.data.local.db.entity.HealthEntryEntity
import io.github.kroune.pollen.data.local.db.entity.LevelEntity
import io.github.kroune.pollen.data.local.db.entity.LocationEntity
import io.github.kroune.pollen.data.local.db.entity.PhenologyEntity
import io.github.kroune.pollen.data.local.db.entity.PollenEntity
import io.github.kroune.pollen.data.local.db.entity.PollenLevelInfoEntity
import io.github.kroune.pollen.data.local.db.entity.StatisticsEntity
import io.github.kroune.pollen.data.local.db.entity.SyncStateEntity
import io.github.kroune.pollen.data.local.db.entity.TherapyEntity
import io.github.kroune.pollen.data.local.db.entity.MedicationIntakeEntity
import io.github.kroune.pollen.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SyncStateEntity::class,
        HealthEntryEntity::class,
        PollenEntity::class,
        PollenLevelInfoEntity::class,
        LocationEntity::class,
        LevelEntity::class,
        ForecastLevelEntity::class,
        StatisticsEntity::class,
        FriendEntity::class,
        TherapyEntity::class,
        PhenologyEntity::class,
        DayActivityEntity::class,
        AllergenSensitivityEntity::class,
        MedicationIntakeEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
@ConstructedBy(PollenDatabaseConstructor::class)
abstract class PollenDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun syncStateDao(): SyncStateDao
    abstract fun healthDao(): HealthDao
    abstract fun pollenDao(): PollenDao
    abstract fun locationDao(): LocationDao
    abstract fun levelDao(): LevelDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun friendDao(): FriendDao
    abstract fun therapyDao(): TherapyDao
    abstract fun phenologyDao(): PhenologyDao
    abstract fun dayActivityDao(): DayActivityDao
    abstract fun allergenSensitivityDao(): AllergenSensitivityDao
    abstract fun medicationIntakeDao(): MedicationIntakeDao

    companion object {
        const val DATABASE_NAME = "pollen_db"
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PollenDatabaseConstructor : RoomDatabaseConstructor<PollenDatabase>
