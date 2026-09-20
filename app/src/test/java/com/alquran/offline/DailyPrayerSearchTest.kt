package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DailyPrayerSearchTest {

    private data class RawPrayer(
        val id: String,
        val category: String,
        val title: String,
        val arabic: String,
        val transliteration: String?,
        val translation: String?,
        val source: String?
    )

    private fun loadPrayers(): List<RawPrayer> {
        val candidates = listOf(
            File("src/main/assets/daily_prayer/daily_prayers.json"),
            File("app/src/main/assets/daily_prayer/daily_prayers.json"),
            File("/root/Alquran/app/src/main/assets/daily_prayer/daily_prayers.json")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertNotNull("daily_prayers.json must exist", file)

        val text = file!!.readText(Charsets.UTF_8)
        val items = mutableListOf<RawPrayer>()
        val objRegex = Regex("\\{[^{}]*\"id\"[^{}]*\\}", RegexOption.DOT_MATCHES_ALL)

        for (match in objRegex.findAll(text)) {
            val block = match.value
            fun extractString(key: String): String {
                val r = Regex("\"$key\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                val raw = r.find(block)?.groupValues?.get(1) ?: ""
                return raw.replace("\\\"", "\"").replace("\\n", "\n")
            }
            val id = extractString("id")
            if (id.isNotBlank()) {
                items.add(
                    RawPrayer(
                        id = id,
                        category = extractString("category"),
                        title = extractString("title"),
                        arabic = extractString("arabic"),
                        transliteration = extractString("transliteration"),
                        translation = extractString("translation"),
                        source = extractString("source")
                    )
                )
            }
        }
        return items
    }

    private fun searchPrayers(prayers: List<RawPrayer>, query: String, category: String? = null): List<RawPrayer> {
        val cleanQuery = query.trim().lowercase()
        val hasCategoryFilter = !category.isNullOrBlank() && !category.equals("Semua", ignoreCase = true)

        return prayers.filter { item ->
            val matchCategory = !hasCategoryFilter || item.category.equals(category, ignoreCase = true)
            if (!matchCategory) return@filter false

            if (cleanQuery.isEmpty()) return@filter true

            item.title.lowercase().contains(cleanQuery) ||
                item.category.lowercase().contains(cleanQuery) ||
                (item.transliteration?.lowercase()?.contains(cleanQuery) == true) ||
                (item.translation?.lowercase()?.contains(cleanQuery) == true) ||
                (item.source?.lowercase()?.contains(cleanQuery) == true) ||
                item.arabic.contains(cleanQuery)
        }
    }

    @Test
    fun testEmptySearchReturnsAllPrayers() {
        val all = loadPrayers()
        val results = searchPrayers(all, "")
        assertEquals(all.size, results.size)
    }

    @Test
    fun testSearchByTitleKeyword() {
        val all = loadPrayers()
        val tidurResults = searchPrayers(all, "tidur")
        assertTrue("Search for 'tidur' should return results", tidurResults.isNotEmpty())
        assertTrue("Results must contain tidur prayer", tidurResults.any { it.title.contains("Tidur") })

        val makanResults = searchPrayers(all, "makan")
        assertTrue("Search for 'makan' should return results", makanResults.isNotEmpty())
    }

    @Test
    fun testSearchByCategory() {
        val all = loadPrayers()
        val catResults = searchPrayers(all, "", "Aktivitas Harian")
        assertTrue("Should return prayers in category", catResults.isNotEmpty())
        assertTrue("All returned prayers must have matching category", catResults.all { it.category == "Aktivitas Harian" })
    }

    @Test
    fun testSearchByTranslationKeyword() {
        val all = loadPrayers()
        val results = searchPrayers(all, "berkah")
        assertTrue("Search by word in translation should find matches", results.isNotEmpty())
    }

    @Test
    fun testNonExistentSearchIsSafeAndReturnsEmpty() {
        val all = loadPrayers()
        val results = searchPrayers(all, "xyznonexistentword12345")
        assertTrue("Non-matching search must return empty list without error", results.isEmpty())
    }
}
