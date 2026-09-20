package com.alquran.offline.data.repository

import com.alquran.offline.model.DailyPrayer
import kotlinx.coroutines.flow.Flow

interface DailyPrayerRepository {
    fun getAllPrayers(): Flow<List<DailyPrayer>>
    suspend fun getAllPrayersList(): List<DailyPrayer>
    suspend fun getPrayerById(id: String): DailyPrayer?
    fun getCategories(): Flow<List<String>>
    fun searchPrayers(query: String, category: String? = null): Flow<List<DailyPrayer>>
}
