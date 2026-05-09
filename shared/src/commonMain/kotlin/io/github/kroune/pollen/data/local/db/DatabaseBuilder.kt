package io.github.kroune.pollen.data.local.db

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(): RoomDatabase.Builder<PollenDatabase>
