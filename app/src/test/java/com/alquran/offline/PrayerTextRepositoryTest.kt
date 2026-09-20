package com.alquran.offline

import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PrayerTextRepositoryTest {

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

    @Test
    fun testPrayerReadingsCountAndStructure() {
        val file = getPrayerJsonFile()
        val content = file.readText(Charsets.UTF_8)
        val jsonArray = JSONArray(content)

        assertTrue("Readings must have at least 12 entries", jsonArray.length() >= 12)

        val seenIds = mutableSetOf<Int>()
        val titles = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getInt("id")
            val title = obj.getString("title")
            val category = obj.getString("category")
            val arabic = obj.getString("arabic")
            val transliteration = obj.getString("transliteration")
            val translation = obj.getString("translation")
            val source = obj.getString("source")

            assertTrue("ID must be positive", id > 0)
            assertTrue("ID must be unique", seenIds.add(id))
            assertTrue("Title must not be blank", title.isNotBlank())
            assertTrue("Category must not be blank", category.isNotBlank())
            assertTrue("Arabic must not be blank", arabic.isNotBlank())
            assertTrue("Transliteration must not be blank", transliteration.isNotBlank())
            assertTrue("Translation must not be blank", translation.isNotBlank())
            assertTrue("Source must not be blank", source.isNotBlank())

            titles.add(title)
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
        val file = getPrayerJsonFile()
        val content = file.readText(Charsets.UTF_8)
        val jsonArray = JSONArray(content)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val source = obj.getString("source").lowercase()
            val valid = source.contains("bukhari") ||
                    source.contains("muslim") ||
                    source.contains("abu daud") ||
                    source.contains("tirmidzi") ||
                    source.contains("ibnu majah") ||
                    source.contains("qs.") ||
                    source.contains("qur'an")

            assertTrue("Source for '${obj.getString("title")}' must refer to verified authentic hadith/Quran: $source", valid)
        }
    }
}
