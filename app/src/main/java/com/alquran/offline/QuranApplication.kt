/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
package com.alquran.offline

import android.app.Application
import com.alquran.offline.data.local.AppDatabase
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.PrayerRepository
import com.alquran.offline.data.repository.PrayerRepositoryImpl
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.data.repository.QuranRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuranApplication : Application() {

    lateinit var repository: QuranRepository
        private set

    lateinit var prayerRepository: PrayerRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val preferencesRepository = UserPreferencesRepository(this)
        val database = AppDatabase.getInstance(this)
        repository = QuranRepositoryImpl(database, preferencesRepository)
        prayerRepository = PrayerRepositoryImpl(this)

        // Asynchronously pre-warm the database on Dispatchers.IO.
        // Room extracts and prepares the asset database in the background
        // without stalling the UI thread during cold start.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.openHelper.writableDatabase
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
