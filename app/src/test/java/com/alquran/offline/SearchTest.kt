package com.alquran.offline

import com.alquran.offline.model.Surah
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchTest {

    private val sampleSurahs = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "Pembukaan", "Makkiyah", 7, 1, 1),
        Surah(2, "البقرة", "Al-Baqarah", "Sapi Betina", "Madaniyah", 286, 1, 3),
        Surah(18, "الكهف", "Al-Kahf", "Gua", "Makkiyah", 110, 15, 16),
        Surah(36, "يس", "Yasin", "Yasin", "Makkiyah", 83, 22, 23),
        Surah(112, "الإخلاص", "Al-Ikhlas", "Keesaan Allah", "Makkiyah", 4, 30, 30)
    )

    @Test
    fun testSearchByLatinName() {
        val query = "kahf"
        val result = sampleSurahs.filter {
            it.nameLatin.lowercase().contains(query.lowercase())
        }
        assertEquals(1, result.size)
        assertEquals("Al-Kahf", result[0].nameLatin)
    }

    @Test
    fun testSearchBySurahNumber() {
        val query = "36"
        val result = sampleSurahs.filter {
            it.id.toString() == query
        }
        assertEquals(1, result.size)
        assertEquals("Yasin", result[0].nameLatin)
    }

    @Test
    fun testSearchByMeaning() {
        val query = "sapi"
        val result = sampleSurahs.filter {
            it.translationId.lowercase().contains(query.lowercase())
        }
        assertEquals(1, result.size)
        assertEquals("Al-Baqarah", result[0].nameLatin)
    }

    @Test
    fun testEmptyQueryReturnsAll() {
        val query = ""
        val result = if (query.isBlank()) sampleSurahs else sampleSurahs.filter {
            it.nameLatin.contains(query)
        }
        assertEquals(5, result.size)
    }
}
