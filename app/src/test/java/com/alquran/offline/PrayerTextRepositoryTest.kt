package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PrayerTextRepositoryTest {

    private data class RawPrayer(
        val id: Int,
        val title: String,
        val category: String,
        val arabic: String,
        val transliteration: String,
        val translation: String,
        val source: String
    )

    private fun getPrayerJsonFile(): File {
        val candidates = listOf(
            File("src/main/assets/prayer/prayer_readings.json"),
            File("app/src/main/assets/prayer/prayer_readings.json"),
            File("/root/Alquran/app/src/main/assets/prayer/prayer_readings.json")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertNotNull("prayer_readings.json must exist in assets", file)
        return file!!
    }

    private fun loadReadings(): List<RawPrayer> {
        val file = getPrayerJsonFile()
        val text = file.readText(Charsets.UTF_8)
        val items = mutableListOf<RawPrayer>()
        val objRegex = Regex("\\{[^{}]*\"id\"[^{}]*\\}", RegexOption.DOT_MATCHES_ALL)

        for (match in objRegex.findAll(text)) {
            val block = match.value
            fun extractString(key: String): String {
                val r = Regex("\"$key\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                val raw = r.find(block)?.groupValues?.get(1) ?: ""
                return raw.replace("\\\"", "\"").replace("\\n", "\n")
            }
            fun extractInt(key: String): Int {
                val r = Regex("\"$key\"\\s*:\\s*(\\d+)")
                return r.find(block)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            val id = extractInt("id")
            if (id > 0) {
                items.add(
                    RawPrayer(
                        id = id,
                        title = extractString("title"),
                        category = extractString("category"),
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

    @Test
    fun testPrayerReadingsCountAndStructure() {
        val readings = loadReadings()

        assertTrue("Readings must have at least 12 entries", readings.size >= 12)

        val seenIds = mutableSetOf<Int>()
        val titles = mutableListOf<String>()

        for (item in readings) {
            assertTrue("ID must be positive", item.id > 0)
            assertTrue("ID must be unique", seenIds.add(item.id))
            assertTrue("Title must not be blank", item.title.isNotBlank())
            assertTrue("Category must not be blank", item.category.isNotBlank())
            assertTrue("Arabic must not be blank", item.arabic.isNotBlank())
            assertTrue("Transliteration must not be blank", item.transliteration.isNotBlank())
            assertTrue("Translation must not be blank", item.translation.isNotBlank())
            assertTrue("Source must not be blank", item.source.isNotBlank())

            titles.add(item.title)
        }

        // Verify key components of prayer are present
        val titleString = titles.joinToString(" ")
        assertTrue("Must include Niat", titleString.contains("Niat"))
        assertTrue("Must include Takbiratul Ihram", titleString.contains("Takbiratul Ihram"))
        assertTrue("Must include Iftitah", titleString.contains("Iftitah"))
        assertTrue("Must include Al-Fatihah", titleString.contains("Al-Fatihah"))
        assertTrue("Must include Ruku", titleString.contains("Ruku"))
        assertTrue("Must include I'tidal", titleString.contains("I'tidal"))
        assertTrue("Must include Sujud", titleString.contains("Sujud"))
        assertTrue("Must include Tasyahud", titleString.contains("Tasyahud"))
        assertTrue("Must include Shalawat", titleString.contains("Shalawat"))
        assertTrue("Must include Salam", titleString.contains("Salam"))
    }

    @Test
    fun testAuthenticSourcesInPrayerReadings() {
        val readings = loadReadings()

        for (item in readings) {
            val source = item.source.lowercase()
            val valid = source.contains("bukhari") ||
                    source.contains("muslim") ||
                    source.contains("abu daud") ||
                    source.contains("tirmidzi") ||
                    source.contains("ibnu majah") ||
                    source.contains("qs.") ||
                    source.contains("qur'an")

            assertTrue("Source for '${item.title}' must refer to verified authentic hadith/Quran: $source", valid)
        }
    }
}

