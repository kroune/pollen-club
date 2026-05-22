package io.github.kroune.pollen.di

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.kroune.pollen.data.local.db.PollenDatabase
import io.github.kroune.pollen.data.local.db.getDatabaseBuilder
import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.data.local.prefs.createPlatformDataStore
import org.koin.dsl.module

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS comments")
        connection.execSQL("DROP TABLE IF EXISTS vk_posts")
        connection.execSQL("DROP TABLE IF EXISTS media")
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """CREATE TABLE IF NOT EXISTS allergen_sensitivity (
                pollen_id INTEGER NOT NULL PRIMARY KEY,
                sensitivity_level INTEGER NOT NULL DEFAULT 0
            )""",
        )
        connection.execSQL(
            """CREATE TABLE IF NOT EXISTS medication_intakes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                therapy_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                taken INTEGER NOT NULL DEFAULT 0
            )""",
        )
        connection.execSQL(
            """CREATE UNIQUE INDEX IF NOT EXISTS index_medication_intakes_therapy_id_date
                ON medication_intakes (therapy_id, date)""",
        )
        connection.execSQL(
            "ALTER TABLE health_entries ADD COLUMN symptom_tags TEXT NOT NULL DEFAULT ''",
        )
    }
}

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE friends ADD COLUMN name TEXT NOT NULL DEFAULT ''")
    }
}

val databaseModule = module {
    single {
        getDatabaseBuilder()
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<PollenDatabase>().userDao() }
    single { get<PollenDatabase>().syncStateDao() }
    single { get<PollenDatabase>().healthDao() }
    single { get<PollenDatabase>().pollenDao() }
    single { get<PollenDatabase>().locationDao() }
    single { get<PollenDatabase>().levelDao() }
    single { get<PollenDatabase>().statisticsDao() }
    single { get<PollenDatabase>().friendDao() }
    single { get<PollenDatabase>().therapyDao() }
    single { get<PollenDatabase>().phenologyDao() }
    single { get<PollenDatabase>().dayActivityDao() }
    single { get<PollenDatabase>().allergenSensitivityDao() }
    single { get<PollenDatabase>().medicationIntakeDao() }

    single { createPlatformDataStore() }
    single { AppPreferences(get()) }
}
