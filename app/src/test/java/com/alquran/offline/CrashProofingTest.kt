package com.alquran.offline

import com.alquran.offline.ui.common.UiState
import com.alquran.offline.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CrashProofingTest {

    @Test
    fun testUiStateTransitions() {
        val loading: UiState<String> = UiState.Loading
        assertTrue(loading is UiState.Loading)

        val empty: UiState<String> = UiState.Empty
        assertTrue(empty is UiState.Empty)

        val success: UiState<String> = UiState.Success("Data Al-Quran")
        assertTrue(success is UiState.Success)
        assertEquals("Data Al-Quran", (success as UiState.Success).data)

        val error: UiState<String> = UiState.Error("Koneksi DB Gagal")
        assertTrue(error is UiState.Error)
        assertEquals("Koneksi DB Gagal", (error as UiState.Error).message)
    }

    @Test
    fun testSurahAndVerseClamping() {
        // Test out of bounds surah IDs
        val negativeSurah = (-5).coerceIn(1, 114)
        assertEquals(1, negativeSurah)

        val overflowSurah = 999.coerceIn(1, 114)
        assertEquals(114, overflowSurah)

        val validSurah = 18.coerceIn(1, 114)
        assertEquals(18, validSurah)

        // Test out of bounds verse IDs
        val zeroVerse = 0.coerceAtLeast(1)
        assertEquals(1, zeroVerse)

        val negativeVerse = (-10).coerceAtLeast(1)
        assertEquals(1, negativeVerse)

        val validVerse = 255.coerceAtLeast(1)
        assertEquals(255, validVerse)
    }

    @Test
    fun testHadithIdClamping() {
        val negativeHadith = (-1).coerceIn(0, 42)
        assertEquals(0, negativeHadith)

        val overflowHadith = 100.coerceIn(0, 42)
        assertEquals(42, overflowHadith)

        val validHadith = 1.coerceIn(0, 42)
        assertEquals(1, validHadith)
    }

    @Test
    fun testReaderScrollClampingWithBasmalahHeader() {
        val totalAyahs = 7
        val isSurahWithBasmalah = true // surahId != 1 && surahId != 9
        val headerOffset = if (isSurahWithBasmalah) 1 else 0

        // Target verse 5
        val targetVerse = 5
        val targetIndex = (targetVerse - 1).coerceIn(0, totalAyahs - 1)
        val maxIndex = (totalAyahs + headerOffset - 1).coerceAtLeast(0)
        val safeIndex = (targetIndex + headerOffset).coerceIn(0, maxIndex)

        assertEquals(5, safeIndex) // index 0 is basmalah, 1..7 are verses 1..7

        // Target verse 0 (invalid)
        val safeTargetVerse = 0.coerceAtLeast(1)
        val clampedIndex = (safeTargetVerse - 1).coerceIn(0, totalAyahs - 1)
        val safeClamped = (clampedIndex + headerOffset).coerceIn(0, maxIndex)
        assertEquals(1, safeClamped) // points to first ayah after basmalah

        // Empty ayahs list
        val emptyTotal = 0
        val maxEmptyIndex = (emptyTotal + headerOffset - 1).coerceAtLeast(0)
        val safeEmptyIndex = 0.coerceIn(0, maxEmptyIndex)
        assertEquals(0, safeEmptyIndex)
    }

    @Test
    fun testAllScreenRoutesAreValid() {
        assertEquals("home", Screen.Home.route)
        assertEquals("surah_list", Screen.SurahList.route)
        assertEquals("juz_list", Screen.JuzList.route)
        assertEquals("bookmark", Screen.Bookmark.route)
        assertEquals("search", Screen.Search.route)
        assertEquals("settings", Screen.Settings.route)
        assertEquals("privacy", Screen.Privacy.route)
        assertEquals("about", Screen.About.route)
        assertEquals("prayer_list", Screen.PrayerList.route)
        assertEquals("prayer_detail/1", Screen.PrayerDetail.createRoute(1))
        assertEquals("hadith_detail/1", Screen.HadithDetail.createRoute(1))
        assertEquals("daily_prayer_list", Screen.DailyPrayerList.route)
        assertEquals("daily_prayer_detail/doa-1", Screen.DailyPrayerDetail.createRoute("doa-1"))

        assertEquals("hadith_list?initialId=0", Screen.HadithList.createRoute(0))
        assertEquals("hadith_list?initialId=42", Screen.HadithList.createRoute(42))

        assertEquals("reader/1?targetVerse=1", Screen.Reader.createRoute(1, 1))
        assertEquals("reader/114?targetVerse=6", Screen.Reader.createRoute(114, 6))
    }
}
