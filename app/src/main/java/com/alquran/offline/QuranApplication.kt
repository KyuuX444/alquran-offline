package com.alquran.offline

import android.app.Application
import com.alquran.offline.data.local.AppDatabase
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.data.repository.QuranRepositoryImpl

class QuranApplication : Application() {

    lateinit var repository: QuranRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        val preferencesRepository = UserPreferencesRepository(this)
        repository = QuranRepositoryImpl(database, preferencesRepository)
    }
}
