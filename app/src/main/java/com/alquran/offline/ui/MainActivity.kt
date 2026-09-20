/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
package com.alquran.offline.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alquran.offline.QuranApplication
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.ThemeMode
import com.alquran.offline.ui.components.AppBottomBar
import com.alquran.offline.ui.navigation.NavGraph
import com.alquran.offline.ui.navigation.Screen
import com.alquran.offline.ui.navigation.navigateSafe
import com.alquran.offline.ui.theme.AlQuranOfflineTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private val pendingIntent = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        pendingIntent.value = intent

        val app = application as QuranApplication
        val repository = app.repository
        val prayerRepository = app.prayerRepository
        val preferencesRepository = UserPreferencesRepository(this)

        setContent {
            val themeMode by repository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            AlQuranOfflineTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val bottomBarRoutes = setOf(
                        Screen.Home.route,
                        Screen.SurahList.route,
                        Screen.JuzList.route,
                        Screen.Bookmark.route
                    )
                    val isHadithRoute = currentRoute?.startsWith("hadith_list") == true
                    val showBottomBar = currentRoute in bottomBarRoutes || isHadithRoute

                    LaunchedEffect(Unit) {
                        pendingIntent.collect { incomingIntent ->
                            if (incomingIntent != null) {
                                pendingIntent.value = null
                                handleIntent(incomingIntent, navController, repository)
                            }
                        }
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomBar(
                                    currentRoute = currentRoute,
                                    onTabSelected = { targetRoute ->
                                        val isAlreadySelected = when {
                                            targetRoute.startsWith("hadith_list") -> currentRoute?.startsWith("hadith_list") == true
                                            else -> currentRoute == targetRoute
                                        }
                                        if (!isAlreadySelected) {
                                            navController.navigateSafe(targetRoute) {
                                                popUpTo(Screen.Home.route) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            repository = repository,
                            prayerRepository = prayerRepository,
                            preferencesRepository = preferencesRepository,
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingIntent.value = intent
    }

    private suspend fun handleIntent(
        intent: Intent,
        navController: NavController,
        repository: QuranRepository
    ) {
        try {
            val uri: Uri = intent.data ?: return
            if (uri.scheme == "alquran") {
                val destination = uri.host ?: uri.authority
                when (destination) {
                    "last_read" -> {
                        try {
                            val lastRead = repository.lastRead.first()
                            val safeSurah = lastRead.surahId.coerceIn(1, 114)
                            val safeVerse = lastRead.verseId.coerceAtLeast(1)
                            navController.navigateSafe(Screen.Reader.createRoute(safeSurah, safeVerse)) {
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            navController.navigateSafe(Screen.SurahList.route) {
                                launchSingleTop = true
                            }
                        }
                    }
                    "surah_list" -> {
                        navController.navigateSafe(Screen.SurahList.route) {
                            launchSingleTop = true
                        }
                    }
                    "hadith" -> {
                        val rawId = uri.getQueryParameter("id")?.toIntOrNull() ?: 0
                        val hadithId = rawId.coerceAtLeast(0)
                        if (hadithId > 0) {
                            navController.navigateSafe(Screen.HadithDetail.createRoute(hadithId)) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigateSafe(Screen.HadithList.createRoute(0)) {
                                launchSingleTop = true
                            }
                        }
                    }
                    "prayer", "bacaan_sholat" -> {
                        navController.navigateSafe(Screen.PrayerList.route) {
                            launchSingleTop = true
                        }
                    }
                    "juz_list" -> {
                        navController.navigateSafe(Screen.JuzList.route) {
                            launchSingleTop = true
                        }
                    }
                    "bookmark" -> {
                        navController.navigateSafe(Screen.Bookmark.route) {
                            launchSingleTop = true
                        }
                    }
                    "search" -> {
                        navController.navigateSafe(Screen.Search.route) {
                            launchSingleTop = true
                        }
                    }
                    "settings" -> {
                        navController.navigateSafe(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    }
                    "about", "privacy" -> {
                        navController.navigateSafe(Screen.Privacy.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
