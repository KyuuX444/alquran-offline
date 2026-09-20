/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
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
