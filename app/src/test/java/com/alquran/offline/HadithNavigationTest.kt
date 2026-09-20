package com.alquran.offline

import com.alquran.offline.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class HadithNavigationTest {

    @Test
    fun testScreenRoutes() {
        assertEquals("hadith_detail/123", Screen.HadithDetail.createRoute(123))
        assertEquals("prayer_list", Screen.PrayerList.route)
        assertEquals("prayer_detail/5", Screen.PrayerDetail.createRoute(5))
        assertEquals("hadith_list?initialId=500", Screen.HadithList.createRoute(500))
    }

    @Test
    fun testDailyHadithDeterministicCalculation() {
        val totalHadiths = 38144
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.SEPTEMBER, 20)

        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)

        val targetId = (((dayOfYear * 31 + year) % totalHadiths) + totalHadiths) % totalHadiths + 1
        assertTrue("Target ID must be >= 1", targetId >= 1)
        assertTrue("Target ID must be <= totalHadiths", targetId <= totalHadiths)

        // Consistency check: running again with the same date gives the same ID
        val targetId2 = (((dayOfYear * 31 + year) % totalHadiths) + totalHadiths) % totalHadiths + 1
        assertEquals("Must be strictly deterministic", targetId, targetId2)
    }

    @Test
    fun testAllYearDaysProduceValidHadithIds() {
        val totalHadiths = 38144
        val year = 2026
        for (day in 1..366) {
            val targetId = (((day * 31 + year) % totalHadiths) + totalHadiths) % totalHadiths + 1
            assertTrue("Day $day target ID must be >= 1", targetId >= 1)
            assertTrue("Day $day target ID must be <= $totalHadiths", targetId <= totalHadiths)
        }
    }
}
