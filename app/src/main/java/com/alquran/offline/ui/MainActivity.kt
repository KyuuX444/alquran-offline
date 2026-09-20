package com.alquran.offline.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.model.ThemeMode
import com.alquran.offline.ui.components.AppBottomBar
import com.alquran.offline.ui.navigation.NavGraph
import com.alquran.offline.ui.navigation.Screen
import com.alquran.offline.ui.theme.AlQuranOfflineTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private val pendingIntent = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingIntent.value = intent

        val app = application as QuranApplication
        val repository = app.repository

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
                        bottomBar = {
                            if (showBottomBar) {
                                AppBottomBar(
                                    currentRoute = currentRoute,
                                    onTabSelected = { targetRoute ->
                                        if (currentRoute != targetRoute) {
                                            navController.navigate(targetRoute) {
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
                            modifier = Modifier.padding(innerPadding)
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
                            navController.navigate(Screen.Reader.createRoute(lastRead.surahId, lastRead.verseId))
                        } catch (e: Exception) {
                            navController.navigate(Screen.SurahList.route)
                        }
                    }
                    "surah_list" -> {
                        navController.navigate(Screen.SurahList.route)
                    }
                    "hadith" -> {
                        val hadithId = uri.getQueryParameter("id")?.toIntOrNull() ?: 0
                        navController.navigate(Screen.HadithList.createRoute(hadithId))
                    }
                    "juz_list" -> {
                        navController.navigate(Screen.JuzList.route)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
