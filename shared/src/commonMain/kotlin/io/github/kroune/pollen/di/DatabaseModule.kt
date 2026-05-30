package io.github.kroune.pollen.di

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.kroune.pollen.data.local.db.PollenDatabase
import io.github.kroune.pollen.data.local.db.getDatabaseBuilder
import io.github.kroune.pollen.data.local.prefs.AppPreferences
import io.github.kroune.pollen.data.local.prefs.UserLocalDataSource
import io.github.kroune.pollen.data.local.prefs.createPlatformDataStore
import io.github.kroune.pollen.data.local.prefs.createUserDataStore
import org.koin.core.qualifier.named
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

// User identity + selected location moved out of Room into a typed DataStore (UserSession).
// Drop the now-unused single-row users table, and make health_entries.location_id nullable
// (a health entry may genuinely have no resolved location; 0 is only used at the API boundary).
private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS users")

        // SQLite can't drop NOT NULL in place — recreate health_entries with a nullable location_id.
        connection.execSQL(
            """CREATE TABLE health_entries_new (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `user_id` INTEGER NOT NULL,
                `date` TEXT NOT NULL,
                `feeling` INTEGER NOT NULL,
                `eyes` INTEGER NOT NULL,
                `nose` INTEGER NOT NULL,
                `throat` INTEGER NOT NULL,
                `lungs` INTEGER NOT NULL,
                `general` INTEGER NOT NULL,
                `other` TEXT NOT NULL,
                `time` INTEGER NOT NULL,
                `location_name` TEXT NOT NULL,
                `is_synced` INTEGER NOT NULL,
                `tags` TEXT NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                `location_id` INTEGER,
                `default_pollen` INTEGER NOT NULL,
                `symptom_tags` TEXT NOT NULL
            )""",
        )
        connection.execSQL(
            """INSERT INTO health_entries_new (
                id, user_id, date, feeling, eyes, nose, throat, lungs, general, other, time,
                location_name, is_synced, tags, latitude, longitude, location_id, default_pollen, symptom_tags
            ) SELECT
                id, user_id, date, feeling, eyes, nose, throat, lungs, general, other, time,
                location_name, is_synced, tags, latitude, longitude, location_id, default_pollen, symptom_tags
            FROM health_entries""",
        )
        connection.execSQL("DROP TABLE health_entries")
        connection.execSQL("ALTER TABLE health_entries_new RENAME TO health_entries")
    }
}

val databaseModule = module {
    single {
        getDatabaseBuilder()
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_5_6)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

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

    // Both stores erase to DataStore::class at runtime, so they must be
    // qualified — otherwise Koin keys them under the same type and the
    // last-registered one shadows the other (ClassCastException at use site).
    single(named("app_prefs")) { createPlatformDataStore() }
    single { AppPreferences(get(named("app_prefs"))) }

    single(named("user_data")) { createUserDataStore() }
    single { UserLocalDataSource(get(named("user_data"))) }
}
