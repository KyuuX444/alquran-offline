package com.alquran.offline.data.repository

import android.content.Context
import com.alquran.offline.model.DailyPrayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class DailyPrayerRepositoryImpl(
    private val context: Context
) : DailyPrayerRepository {

    @Volatile
    private var cachedPrayers: List<DailyPrayer>? = null

    private fun loadPrayersFromAsset(): List<DailyPrayer> {
        val cached = cachedPrayers
        if (cached != null) return cached

        synchronized(this) {
            val doubleCheck = cachedPrayers
            if (doubleCheck != null) return doubleCheck

            return try {
                val inputStream = context.assets.open("daily_prayer/daily_prayers.json")
                val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                val jsonText = reader.use { it.readText() }
                val jsonArray = JSONArray(jsonText)
                val list = ArrayList<DailyPrayer>(jsonArray.length())

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        DailyPrayer(
                            id = obj.optString("id", "doa-$i"),
                            category = obj.optString("category", "Umum"),
                            title = obj.optString("title", ""),
                            arabic = obj.optString("arabic", ""),
                            transliteration = obj.optString("transliteration", ""),
                            translation = obj.optString("translation", ""),
                            source = obj.optString("source", ""),
                            reference = obj.optString("reference", "")
                        )
                    )
                }
                cachedPrayers = list
                list
            } catch (e: Throwable) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    override fun getAllPrayers(): Flow<List<DailyPrayer>> = flow {
        emit(loadPrayersFromAsset())
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllPrayersList(): List<DailyPrayer> = withContext(Dispatchers.IO) {
        loadPrayersFromAsset()
    }

    override suspend fun getPrayerById(id: String): DailyPrayer? = withContext(Dispatchers.IO) {
        loadPrayersFromAsset().firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    override fun getCategories(): Flow<List<String>> = flow {
        val prayers = loadPrayersFromAsset()
        val categories = mutableListOf("Semua")
        prayers.map { it.category }.distinct().forEach {
            if (!categories.contains(it)) {
                categories.add(it)
            }
        }
        emit(categories)
    }.flowOn(Dispatchers.IO)

    override fun searchPrayers(query: String, category: String?): Flow<List<DailyPrayer>> = flow {
        val all = loadPrayersFromAsset()
        val cleanQuery = query.trim().lowercase()
        val hasCategoryFilter = !category.isNullOrBlank() && !category.equals("Semua", ignoreCase = true)

        val filtered = all.filter { item ->
            val matchCategory = !hasCategoryFilter || item.category.equals(category, ignoreCase = true)
            if (!matchCategory) return@filter false

            if (cleanQuery.isEmpty()) return@filter true

            item.title.lowercase().contains(cleanQuery) ||
                item.category.lowercase().contains(cleanQuery) ||
                (item.transliteration?.lowercase()?.contains(cleanQuery) == true) ||
                (item.translation?.lowercase()?.contains(cleanQuery) == true) ||
                (item.source?.lowercase()?.contains(cleanQuery) == true)
        }
        emit(filtered)
    }.flowOn(Dispatchers.IO)
}
