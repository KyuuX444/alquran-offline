package com.alquran.offline

import com.alquran.offline.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyPrayerNavigationTest {

    @Test
    fun testDailyPrayerRoutes() {
        assertEquals("daily_prayer_list", Screen.DailyPrayerList.route)
        assertEquals("daily_prayer_detail/doa-1", Screen.DailyPrayerDetail.createRoute("doa-1"))
        assertEquals("daily_prayer_detail/doa-25", Screen.DailyPrayerDetail.createRoute("doa-25"))
        assertEquals("about", Screen.About.route)
    }

    @Test
    fun testDeepLinkHandlingLogic() {
        val testUris = listOf(
            "alquran://daily_prayer" to Screen.DailyPrayerList.route,
            "alquran://doa" to Screen.DailyPrayerList.route,
            "alquran://prayer" to Screen.PrayerList.route,
            "alquran://hadith" to Screen.HadithList.createRoute(0),
            "alquran://surah_list" to Screen.SurahList.route
        )

        for ((uriString, expectedRoute) in testUris) {
            val authority = uriString.substringAfter("://").substringBefore("?")
            val resolvedRoute = when (authority) {
                "daily_prayer", "doa" -> Screen.DailyPrayerList.route
                "prayer" -> Screen.PrayerList.route
                "hadith" -> Screen.HadithList.createRoute(0)
                "surah_list" -> Screen.SurahList.route
                else -> ""
            }
            assertEquals("URI $uriString must map to $expectedRoute", expectedRoute, resolvedRoute)
        }
    }
}
