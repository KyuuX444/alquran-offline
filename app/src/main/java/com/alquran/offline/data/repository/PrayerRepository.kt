package com.alquran.offline.data.repository

import com.alquran.offline.model.PrayerReading
import kotlinx.coroutines.flow.Flow

interface PrayerRepository {
    fun getAllReadings(): Flow<List<PrayerReading>>
    suspend fun getAllReadingsList(): List<PrayerReading>
    suspend fun getReadingById(id: Int): PrayerReading?
    fun getCategories(): Flow<List<String>>
    fun getReadingsByCategory(category: String): Flow<List<PrayerReading>>
}
