package com.alquran.offline.data.repository

import android.content.Context
import com.alquran.offline.model.AsmaulHusna
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class AsmaulHusnaRepositoryImpl(
    private val context: Context
) : AsmaulHusnaRepository {

    @Volatile
    private var cachedItems: List<AsmaulHusna>? = null

    private fun loadFromAsset(): List<AsmaulHusna> {
        val cached = cachedItems
        if (cached != null) return cached

        synchronized(this) {
            val doubleCheck = cachedItems
            if (doubleCheck != null) return doubleCheck

            return try {
                val inputStream = context.assets.open("asmaul_husna/asmaul_husna.json")
                val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                val jsonText = reader.use { it.readText() }
                val jsonArray = JSONArray(jsonText)
                val list = ArrayList<AsmaulHusna>(jsonArray.length())

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        AsmaulHusna(
                            id = obj.optInt("id", i + 1),
                            nameAr = obj.optString("nameAr", ""),
                            nameLatin = obj.optString("nameLatin", ""),
                            meaningId = obj.optString("meaningId", "")
                        )
                    )
                }
                cachedItems = list
                list
            } catch (e: Throwable) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    override fun getAllAsmaulHusna(): Flow<List<AsmaulHusna>> = flow {
        emit(loadFromAsset())
    }.flowOn(Dispatchers.IO)

    override suspend fun getAsmaulHusnaById(id: Int): AsmaulHusna? = withContext(Dispatchers.IO) {
        loadFromAsset().firstOrNull { it.id == id }
    }

    override fun searchAsmaulHusna(query: String): Flow<List<AsmaulHusna>> = flow {
        val all = loadFromAsset()
        val cleanQuery = query.trim().lowercase()

        if (cleanQuery.isEmpty()) {
            emit(all)
        } else {
            val filtered = all.filter { item ->
                item.nameLatin.lowercase().contains(cleanQuery) ||
                    item.meaningId.lowercase().contains(cleanQuery) ||
                    item.id.toString() == cleanQuery
            }
            emit(filtered)
        }
    }.flowOn(Dispatchers.IO)
}
