package com.alquran.offline

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alquran.offline.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationRoutes() {
        assertEquals("home", Screen.Home.route)
        assertEquals("surah_list", Screen.SurahList.route)
        assertEquals("juz_list", Screen.JuzList.route)
        assertEquals("bookmark", Screen.Bookmark.route)
        assertEquals("search", Screen.Search.route)
        assertEquals("settings", Screen.Settings.route)
        assertEquals("privacy", Screen.Privacy.route)

        val readerRoute = Screen.Reader.createRoute(1, 5)
        assertEquals("reader/1?targetVerse=5", readerRoute)

        val hadithRoute = Screen.HadithList.createRoute(3)
        assertEquals("hadith_list?initialId=3", hadithRoute)
    }

    @Test
    fun testNavControllerInitialization() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            assertEquals(null, navController.currentDestination)
        }
    }
}
