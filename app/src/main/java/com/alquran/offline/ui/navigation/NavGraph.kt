package com.alquran.offline.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.ui.screens.bookmark.BookmarkScreen
import com.alquran.offline.ui.screens.bookmark.BookmarkViewModel
import com.alquran.offline.ui.screens.hadith.HadithListScreen
import com.alquran.offline.ui.screens.hadith.HadithListViewModel
import com.alquran.offline.ui.screens.home.HomeScreen
import com.alquran.offline.ui.screens.home.HomeViewModel
import com.alquran.offline.ui.screens.juz.JuzListScreen
import com.alquran.offline.ui.screens.juz.JuzListViewModel
import com.alquran.offline.ui.screens.privacy.PrivacyScreen
import com.alquran.offline.ui.screens.reader.ReaderScreen
import com.alquran.offline.ui.screens.reader.ReaderViewModel
import com.alquran.offline.ui.screens.search.SearchScreen
import com.alquran.offline.ui.screens.search.SearchViewModel
import com.alquran.offline.ui.screens.settings.SettingsScreen
import com.alquran.offline.ui.screens.settings.SettingsViewModel
import com.alquran.offline.ui.screens.surah.SurahListScreen
import com.alquran.offline.ui.screens.surah.SurahListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: QuranRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSurahList = { navController.navigate(Screen.SurahList.route) },
                onNavigateToJuzList = { navController.navigate(Screen.JuzList.route) },
                onNavigateToHadith = { navController.navigate(Screen.HadithList.createRoute()) },
                onNavigateToBookmark = { navController.navigate(Screen.Bookmark.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                onNavigateToReader = { surahId, verseId ->
                    navController.navigate(Screen.Reader.createRoute(surahId, verseId))
                }
            )
        }

        // Surah List
        composable(Screen.SurahList.route) {
            val viewModel: SurahListViewModel = viewModel(
                factory = SurahListViewModel.Factory(repository)
            )
            SurahListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSurahClick = { surahId ->
                    navController.navigate(Screen.Reader.createRoute(surahId, 1))
                },
                onNavigateToJuz = {
                    navController.navigate(Screen.JuzList.route) {
                        popUpTo(Screen.SurahList.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Juz List
        composable(Screen.JuzList.route) {
            val viewModel: JuzListViewModel = viewModel(
                factory = JuzListViewModel.Factory(repository)
            )
            JuzListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onJuzClick = { surahId, verseId ->
                    navController.navigate(Screen.Reader.createRoute(surahId, verseId))
                },
                onNavigateToSurah = {
                    navController.navigate(Screen.SurahList.route) {
                        popUpTo(Screen.JuzList.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Reader Screen
        composable(
            route = Screen.Reader.route,
            arguments = listOf(
                navArgument("surahId") { type = NavType.IntType },
                navArgument("targetVerse") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val rawSurahId = backStackEntry.arguments?.getInt("surahId") ?: 1
            val surahId = rawSurahId.coerceIn(1, 114)
            val rawTargetVerse = backStackEntry.arguments?.getInt("targetVerse") ?: 1
            val targetVerse = rawTargetVerse.coerceAtLeast(1)
            val viewModel: ReaderViewModel = viewModel(
                key = "reader_${surahId}_$targetVerse",
                factory = ReaderViewModel.Factory(repository, surahId, targetVerse)
            )
            ReaderScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Bookmark Screen
        composable(Screen.Bookmark.route) {
            val viewModel: BookmarkViewModel = viewModel(
                factory = BookmarkViewModel.Factory(repository)
            )
            BookmarkScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onBookmarkClick = { surahId, verseId ->
                    navController.navigate(Screen.Reader.createRoute(surahId, verseId))
                }
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            val viewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.Factory(repository)
            )
            SearchScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToReader = { surahId, verseId ->
                    navController.navigate(Screen.Reader.createRoute(surahId, verseId))
                }
            )
        }

        // Hadith Screen
        composable(
            route = Screen.HadithList.route,
            arguments = listOf(
                navArgument("initialId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val rawInitialId = backStackEntry.arguments?.getInt("initialId") ?: 0
            val initialId = rawInitialId.coerceIn(0, 42)
            val viewModel: HadithListViewModel = viewModel(
                key = "hadith_$initialId",
                factory = HadithListViewModel.Factory(repository, if (initialId > 0) initialId else null)
            )
            HadithListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            val context = LocalContext.current
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(repository, context.applicationContext)
            )
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onPrivacyClick = { navController.navigate(Screen.Privacy.route) }
            )
        }

        // Privacy Screen
        composable(Screen.Privacy.route) {
            PrivacyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
