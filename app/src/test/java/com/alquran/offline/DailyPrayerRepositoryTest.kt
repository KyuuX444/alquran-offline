package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DailyPrayerRepositoryTest {

    private data class RawPrayer(
        val id: String,
        val category: String,
        val title: String,
        val arabic: String,
        val transliteration: String?,
        val translation: String?,
        val source: String?,
        val reference: String?
    )

    private fun getDailyPrayerFile(): File {
        val candidates = listOf(
            File("src/main/assets/daily_prayer/daily_prayers.json"),
            File("app/src/main/assets/daily_prayer/daily_prayers.json"),
            File("/root/Alquran/app/src/main/assets/daily_prayer/daily_prayers.json")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertNotNull("daily_prayers.json must exist in assets", file)
        return file!!
    }

    private fun loadPrayers(): List<RawPrayer> {
        val file = getDailyPrayerFile()
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
                        source = extractString("source"),
                        reference = extractString("reference")
                    )
                )
            }
        }
        return items
    }

    @Test
    fun testDailyPrayersCountAndCompleteness() {
        val prayers = loadPrayers()
        assertTrue("Must contain at least 30 authentic daily prayers", prayers.size >= 30)

        val seenIds = mutableSetOf<String>()
        val titles = mutableListOf<String>()

        for (item in prayers) {
            assertTrue("ID must not be blank", item.id.isNotBlank())
            assertTrue("ID must be unique: ${item.id}", seenIds.add(item.id))
            assertTrue("Title must not be blank", item.title.isNotBlank())
            assertTrue("Category must not be blank", item.category.isNotBlank())
            assertTrue("Arabic must not be blank", item.arabic.isNotBlank())
            assertTrue("Transliteration must not be blank", !item.transliteration.isNullOrBlank())
            assertTrue("Translation must not be blank", !item.translation.isNullOrBlank())
            assertTrue("Source must not be blank", !item.source.isNullOrBlank())

            titles.add(item.title)
        }

        // Verify key daily activities are covered
        val titleString = titles.joinToString(" ")
        assertTrue("Must include Tidur", titleString.contains("Tidur"))
        assertTrue("Must include Makan", titleString.contains("Makan"))
        assertTrue("Must include Rumah", titleString.contains("Rumah"))
        assertTrue("Must include Masjid", titleString.contains("Masjid"))
        assertTrue("Must include Wudhu", titleString.contains("Wudhu"))
        assertTrue("Must include Safar/Bepergian", titleString.contains("Bepergian") || titleString.contains("Safar"))
        assertTrue("Must include Belajar", titleString.contains("Belajar"))
        assertTrue("Must include Kerja/Ikhtiar", titleString.contains("Bekerja"))
        assertTrue("Must include Ilmu", titleString.contains("Ilmu"))
        assertTrue("Must include Rezeki", titleString.contains("Rezeki"))
        assertTrue("Must include Orang Tua", titleString.contains("Orang Tua"))
        assertTrue("Must include Dunia dan Akhirat", titleString.contains("Dunia dan Akhirat"))
        assertTrue("Must include Ampunan", titleString.contains("Ampunan"))
        assertTrue("Must include Sedih/Kesulitan", titleString.contains("Sedih"))
        assertTrue("Must include Sakit/Kesembuhan", titleString.contains("Sakit") || titleString.contains("Kesembuhan"))
        assertTrue("Must include Hujan", titleString.contains("Hujan"))
        assertTrue("Must include Petir", titleString.contains("Petir"))
        assertTrue("Must include Perlindungan", titleString.contains("Perlindungan"))
        assertTrue("Must include Ketenangan", titleString.contains("Ketenangan"))
    }

    @Test
    fun testAuthenticSourcesVerification() {
        val prayers = loadPrayers()
        for (item in prayers) {
            val src = (item.source ?: "").lowercase()
            val valid = src.contains("bukhari") ||
                    src.contains("muslim") ||
                    src.contains("abu daud") ||
                    src.contains("tirmidzi") ||
                    src.contains("ibnu majah") ||
                    src.contains("malik") ||
                    src.contains("ahmad") ||
                    src.contains("ibnu hibban") ||
                    src.contains("qs.")

            assertTrue("Source for '${item.title}' must be verified classical source: $src", valid)
        }
    }
}
