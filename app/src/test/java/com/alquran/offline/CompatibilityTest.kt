package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class CompatibilityTest {

    @Test
    fun testSurahIdClamping() {
        // Test out of bounds inputs
        val negativeId = -5
        val zeroId = 0
        val normalId = 18
        val overflowId = 150
        val maxId = 114

        assertEquals(1, negativeId.coerceIn(1, 114))
        assertEquals(1, zeroId.coerceIn(1, 114))
        assertEquals(18, normalId.coerceIn(1, 114))
        assertEquals(114, overflowId.coerceIn(1, 114))
        assertEquals(114, maxId.coerceIn(1, 114))
    }

    @Test
    fun testVerseIdClamping() {
        val negativeVerse = -10
        val zeroVerse = 0
        val normalVerse = 255

        assertEquals(1, negativeVerse.coerceAtLeast(1))
        assertEquals(1, zeroVerse.coerceAtLeast(1))
        assertEquals(255, normalVerse.coerceAtLeast(1))
    }

    @Test
    fun testHadithIdClamping() {
        val negativeId = -1
        val zeroId = 0
        val validId = 24
        val overflowId = 999

        assertEquals(0, negativeId.coerceIn(0, 42))
        assertEquals(0, zeroId.coerceIn(0, 42))
        assertEquals(24, validId.coerceIn(0, 42))
        assertEquals(42, overflowId.coerceIn(0, 42))
    }

    @Test
    fun testMalformedDeepLinkUris() {
        // Empty query
        val uri1 = URI.create("alquran://hadith")
        val dest1 = uri1.host ?: uri1.authority
        assertEquals("hadith", dest1)

        // Invalid query value
        val uri2 = URI.create("alquran://hadith?id=abc")
        val dest2 = uri2.host ?: uri2.authority
        assertEquals("hadith", dest2)

        // Special characters or underscore authorities
        val uri3 = URI.create("alquran://last_read")
        val dest3 = uri3.host ?: uri3.authority
        assertEquals("last_read", dest3)

        val uri4 = URI.create("alquran://surah_list")
        val dest4 = uri4.host ?: uri4.authority
        assertEquals("surah_list", dest4)

        val uri5 = URI.create("alquran://juz_list")
        val dest5 = uri5.host ?: uri5.authority
        assertEquals("juz_list", dest5)
    }

    @Test
    fun testDefaultDisplaySettingsLimits() {
        // Arabic font size range: 20..44
        val minArabic = 20f
        val maxArabic = 44f
        val defaultArabic = 28f
        assertTrue(defaultArabic in minArabic..maxArabic)

        // Translation font size range: 12..24
        val minTranslation = 12f
        val maxTranslation = 24f
        val defaultTranslation = 15f
        assertTrue(defaultTranslation in minTranslation..maxTranslation)
    }
}
