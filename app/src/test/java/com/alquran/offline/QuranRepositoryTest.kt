package com.alquran.offline

import com.alquran.offline.data.local.entity.AyahEntity
import com.alquran.offline.data.local.entity.BookmarkEntity
import com.alquran.offline.data.local.entity.SurahEntity
import com.alquran.offline.model.LastRead
import com.alquran.offline.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranRepositoryTest {

    @Test
    fun testSurahEntityToDomainMapping() {
        val entity = SurahEntity(
            id = 1,
            nameAr = "الفاتحة",
            nameLatin = "Al-Fatihah",
            translationId = "Pembukaan",
            type = "Makkiyah",
            totalVerses = 7,
            juzStart = 1,
            juzEnd = 1
        )
        val domain = entity.toDomain()
        assertEquals(1, domain.id)
        assertEquals("Al-Fatihah", domain.nameLatin)
        assertEquals("الفاتحة", domain.nameAr)
        assertEquals(7, domain.totalVerses)
        assertEquals("Makkiyah", domain.type)
    }

    @Test
    fun testAyahEntityToDomainMapping() {
        val entity = AyahEntity(
            id = 1,
            surahId = 1,
            verseId = 1,
            juzId = 1,
            textAr = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            textId = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang",
            transliteration = "Bismi Allahi alrrahmani alrraheemi"
        )
        val domain = entity.toDomain(isBookmarked = true)
        assertEquals(1, domain.verseId)
        assertTrue(domain.isBookmarked)
        assertEquals(1, domain.juzId)
        assertEquals("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", domain.textAr)
    }

    @Test
    fun testBookmarkEntityToDomainMapping() {
        val entity = BookmarkEntity(
            id = 10,
            surahId = 18,
            verseId = 10,
            createdAt = 1000L,
            note = "Ayat hafalan"
        )
        val domain = entity.toDomain(
            surahName = "Al-Kahf",
            textAr = "...",
            textId = "..."
        )
        assertEquals(10L, domain.id)
        assertEquals(18, domain.surahId)
        assertEquals("Al-Kahf", domain.surahNameLatin)
        assertEquals("Ayat hafalan", domain.note)
    }

    @Test
    fun testLastReadDefaultValues() {
        val lastRead = LastRead()
        assertEquals(1, lastRead.surahId)
        assertEquals("Al-Fatihah", lastRead.surahNameLatin)
        assertEquals(1, lastRead.verseId)
    }

    @Test
    fun testThemeModes() {
        assertEquals("SYSTEM", ThemeMode.SYSTEM.name)
        assertEquals("LIGHT", ThemeMode.LIGHT.name)
        assertEquals("DARK", ThemeMode.DARK.name)
    }
}
