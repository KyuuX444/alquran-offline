package com.alquran.offline

import com.alquran.offline.data.local.entity.BookmarkEntity
import com.alquran.offline.data.local.entity.HadithBookmarkEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkTest {

    @Test
    fun testQuranBookmarkCreationAndMapping() {
        val entity = BookmarkEntity(
            id = 1L,
            surahId = 2,
            verseId = 255,
            createdAt = 1700000000L,
            note = "Ayat Kursi"
        )
        val domain = entity.toDomain(
            surahName = "Al-Baqarah",
            textAr = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            textId = "Allah, tidak ada tuhan selain Dia. Yang Mahahidup, yang terus-menerus mengurus makhluk-Nya"
        )

        assertEquals(1L, domain.id)
        assertEquals(2, domain.surahId)
        assertEquals(255, domain.verseId)
        assertEquals("Al-Baqarah", domain.surahNameLatin)
        assertEquals("Ayat Kursi", domain.note)
        assertTrue(domain.textAr.contains("اللَّهُ"))
    }

    @Test
    fun testBookmarkCollectionAddRemove() {
        val bookmarks = mutableListOf<BookmarkEntity>()

        // Add Bookmark
        val bm1 = BookmarkEntity(id = 1L, surahId = 1, verseId = 1, createdAt = 1000L)
        val bm2 = BookmarkEntity(id = 2L, surahId = 18, verseId = 10, createdAt = 2000L)
        bookmarks.add(bm1)
        bookmarks.add(bm2)

        assertEquals(2, bookmarks.size)
        assertTrue(bookmarks.any { it.surahId == 18 && it.verseId == 10 })

        // Remove Bookmark
        bookmarks.removeAll { it.surahId == 1 && it.verseId == 1 }
        assertEquals(1, bookmarks.size)
        assertFalse(bookmarks.any { it.surahId == 1 && it.verseId == 1 })
        assertTrue(bookmarks.any { it.surahId == 18 && it.verseId == 10 })
    }

    @Test
    fun testHadithBookmarkEntity() {
        val hadithBm = HadithBookmarkEntity(
            id = 10L,
            hadithId = 1,
            createdAt = 123456789L,
            note = "Hadits tentang niat"
        )

        assertEquals(10L, hadithBm.id)
        assertEquals(1, hadithBm.hadithId)
        assertEquals(123456789L, hadithBm.createdAt)
        assertEquals("Hadits tentang niat", hadithBm.note)
    }

    @Test
    fun testPreventDuplicateBookmarksInSet() {
        val bookmarkKeys = mutableSetOf<String>()

        fun addBookmark(surahId: Int, verseId: Int): Boolean {
            val key = "${surahId}_$verseId"
            return bookmarkKeys.add(key)
        }

        assertTrue("First add should succeed", addBookmark(2, 255))
        assertFalse("Duplicate add should return false", addBookmark(2, 255))
        assertEquals(1, bookmarkKeys.size)
    }
}
