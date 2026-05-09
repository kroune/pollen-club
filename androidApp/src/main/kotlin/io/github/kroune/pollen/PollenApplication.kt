package io.github.kroune.pollen

import android.app.Application
import io.github.kroune.pollen.data.local.db.appContext
import io.github.kroune.pollen.di.initKoin

class PollenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        initKoin()
    }
}
