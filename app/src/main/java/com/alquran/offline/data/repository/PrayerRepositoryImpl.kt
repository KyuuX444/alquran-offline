package com.alquran.offline.data.repository

import android.content.Context
import com.alquran.offline.model.PrayerReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class PrayerRepositoryImpl(
    private val context: Context
) : PrayerRepository {

    @Volatile
    private var cachedReadings: List<PrayerReading>? = null

    private fun loadReadingsFromAsset(): List<PrayerReading> {
        val cached = cachedReadings
        if (cached != null) return cached

        synchronized(this) {
            val doubleCheck = cachedReadings
            if (doubleCheck != null) return doubleCheck

            return try {
                val inputStream = context.assets.open("prayer/prayer_readings.json")
                val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                val jsonText = reader.use { it.readText() }
                val jsonArray = JSONArray(jsonText)
                val list = ArrayList<PrayerReading>(jsonArray.length())

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        PrayerReading(
                            id = obj.getInt("id"),
                            title = obj.getString("title"),
                            category = obj.getString("category"),
                            arabic = obj.getString("arabic"),
                            transliteration = obj.getString("transliteration"),
                            translation = obj.getString("translation"),
                            source = obj.getString("source"),
                            explanation = obj.optString("explanation", "")
                        )
                    )
                }
                cachedReadings = list
                list
            } catch (e: Throwable) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    override fun getAllReadings(): Flow<List<PrayerReading>> = flow {
        emit(loadReadingsFromAsset())
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllReadingsList(): List<PrayerReading> = withContext(Dispatchers.IO) {
        loadReadingsFromAsset()
    }

    override suspend fun getReadingById(id: Int): PrayerReading? = withContext(Dispatchers.IO) {
        loadReadingsFromAsset().firstOrNull { it.id == id }
    }

    override fun getCategories(): Flow<List<String>> = flow {
        val readings = loadReadingsFromAsset()
        val categories = mutableListOf("Semua")
        readings.map { it.category }.distinct().forEach {
            if (!categories.contains(it)) {
                categories.add(it)
            }
        }
        emit(categories)
    }.flowOn(Dispatchers.IO)

    override fun getReadingsByCategory(category: String): Flow<List<PrayerReading>> = flow {
        val readings = loadReadingsFromAsset()
        if (category == "Semua" || category.isBlank()) {
            emit(readings)
        } else {
            emit(readings.filter { it.category.equals(category, ignoreCase = true) })
        }
    }.flowOn(Dispatchers.IO)
}
