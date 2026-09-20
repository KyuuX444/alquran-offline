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
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.data.repository.AsmaulHusnaRepository
import com.alquran.offline.data.repository.DailyPrayerRepository
import com.alquran.offline.data.repository.PrayerRepository
import com.alquran.offline.data.repository.QuranRepository
import com.alquran.offline.ui.screens.about.AboutScreen
import com.alquran.offline.ui.screens.asmaul_husna.AsmaulHusnaScreen
import com.alquran.offline.ui.screens.asmaul_husna.AsmaulHusnaViewModel
import com.alquran.offline.ui.screens.bookmark.BookmarkScreen
import com.alquran.offline.ui.screens.bookmark.BookmarkViewModel
import com.alquran.offline.ui.screens.daily_prayer.DailyPrayerDetailScreen
import com.alquran.offline.ui.screens.daily_prayer.DailyPrayerDetailViewModel
import com.alquran.offline.ui.screens.daily_prayer.DailyPrayerListScreen
import com.alquran.offline.ui.screens.daily_prayer.DailyPrayerListViewModel
import com.alquran.offline.ui.screens.hadith.HadithDetailScreen
import com.alquran.offline.ui.screens.hadith.HadithDetailViewModel
import com.alquran.offline.ui.screens.hadith.HadithListScreen
import com.alquran.offline.ui.screens.hadith.HadithListViewModel
import com.alquran.offline.ui.screens.home.HomeScreen
import com.alquran.offline.ui.screens.home.HomeViewModel
import com.alquran.offline.ui.screens.juz.JuzListScreen
import com.alquran.offline.ui.screens.juz.JuzListViewModel
import com.alquran.offline.ui.screens.prayer.PrayerDetailScreen
import com.alquran.offline.ui.screens.prayer.PrayerDetailViewModel
import com.alquran.offline.ui.screens.prayer.PrayerListScreen
import com.alquran.offline.ui.screens.prayer.PrayerListViewModel
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
    prayerRepository: PrayerRepository,
    dailyPrayerRepository: DailyPrayerRepository,
    asmaulHusnaRepository: AsmaulHusnaRepository,
    preferencesRepository: UserPreferencesRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSurahList = { navController.navigateSafe(Screen.SurahList.route) },
                onNavigateToJuzList = { navController.navigateSafe(Screen.JuzList.route) },
                onNavigateToHadith = { navController.navigateSafe(Screen.HadithList.createRoute()) },
                onNavigateToHadithDetail = { hadithId ->
                    navController.navigateSafe(Screen.HadithDetail.createRoute(hadithId))
                },
                onNavigateToPrayerList = { navController.navigateSafe(Screen.PrayerList.route) },
                onNavigateToDailyPrayer = { navController.navigateSafe(Screen.DailyPrayerList.route) },
                onNavigateToAsmaulHusna = { navController.navigateSafe(Screen.AsmaulHusna.route) },
                onNavigateToBookmark = { navController.navigateSafe(Screen.Bookmark.route) },
                onNavigateToSearch = { navController.navigateSafe(Screen.Search.route) },
                onNavigateToSettings = { navController.navigateSafe(Screen.Settings.route) },
                onNavigateToPrivacy = { navController.navigateSafe(Screen.About.route) },
                onNavigateToReader = { surahId, verseId ->
                    navController.navigateSafe(Screen.Reader.createRoute(surahId, verseId))
                }
            )
        }

        // Surah List Screen
        composable(Screen.SurahList.route) {
            val viewModel: SurahListViewModel = viewModel(
                factory = SurahListViewModel.Factory(repository)
            )
            SurahListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onSurahClick = { surahId ->
                    navController.navigateSafe(Screen.Reader.createRoute(surahId, 1))
                },
                onNavigateToJuz = {
                    navController.navigateSafe(Screen.JuzList.route) {
                        popUpTo(Screen.SurahList.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Juz List Screen
        composable(Screen.JuzList.route) {
            val viewModel: JuzListViewModel = viewModel(
                factory = JuzListViewModel.Factory(repository)
            )
            JuzListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onJuzClick = { surahId, verseId ->
                    navController.navigateSafe(Screen.Reader.createRoute(surahId, verseId))
                },
                onNavigateToSurah = {
                    navController.navigateSafe(Screen.SurahList.route) {
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
                onBackClick = { navController.popBackStackSafe() },
                onHomeClick = { navController.navigateToHome() },
                onNavigateToSurah = { nextSurahId ->
                    navController.navigateSafe(Screen.Reader.createRoute(nextSurahId, 1)) {
                        popUpTo(Screen.Reader.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = { navController.navigateSafe(Screen.Settings.route) }
            )
        }

        // Bookmark Screen
        composable(Screen.Bookmark.route) {
            val viewModel: BookmarkViewModel = viewModel(
                factory = BookmarkViewModel.Factory(repository)
            )
            BookmarkScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onBookmarkClick = { surahId, verseId ->
                    navController.navigateSafe(Screen.Reader.createRoute(surahId, verseId))
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
                onBackClick = { navController.popBackStackSafe() },
                onNavigateToReader = { surahId, verseId ->
                    navController.navigateSafe(Screen.Reader.createRoute(surahId, verseId))
                }
            )
        }

        // Hadith List Screen
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
            val initialId = rawInitialId.coerceAtLeast(0)
            val viewModel: HadithListViewModel = viewModel(
                key = "hadith_$initialId",
                factory = HadithListViewModel.Factory(repository, if (initialId > 0) initialId else null)
            )
            HadithListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() }
            )
        }

        // Hadith Detail Screen (Direct View from Daily Hadith or selection)
        composable(
            route = Screen.HadithDetail.route,
            arguments = listOf(
                navArgument("hadithId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val rawHadithId = backStackEntry.arguments?.getInt("hadithId") ?: 1
            val hadithId = rawHadithId.coerceAtLeast(1)
            val viewModel: HadithDetailViewModel = viewModel(
                key = "hadith_detail_$hadithId",
                factory = HadithDetailViewModel.Factory(repository, hadithId)
            )
            HadithDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() }
            )
        }

        // Bacaan Sholat List Screen
        composable(Screen.PrayerList.route) {
            val viewModel: PrayerListViewModel = viewModel(
                factory = PrayerListViewModel.Factory(prayerRepository)
            )
            PrayerListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onReadingClick = { prayerId ->
                    navController.navigateSafe(Screen.PrayerDetail.createRoute(prayerId))
                }
            )
        }

        // Bacaan Sholat Detail Screen
        composable(
            route = Screen.PrayerDetail.route,
            arguments = listOf(
                navArgument("prayerId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val rawPrayerId = backStackEntry.arguments?.getInt("prayerId") ?: 1
            val prayerId = rawPrayerId.coerceAtLeast(1)
            val viewModel: PrayerDetailViewModel = viewModel(
                key = "prayer_$prayerId",
                factory = PrayerDetailViewModel.Factory(prayerRepository, preferencesRepository, prayerId)
            )
            PrayerDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() }
            )
        }

        // Daily Prayer List Screen (Doa Harian)
        composable(Screen.DailyPrayerList.route) {
            val viewModel: DailyPrayerListViewModel = viewModel(
                factory = DailyPrayerListViewModel.Factory(dailyPrayerRepository)
            )
            DailyPrayerListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onPrayerClick = { prayerId ->
                    navController.navigateSafe(Screen.DailyPrayerDetail.createRoute(prayerId))
                }
            )
        }

        // Daily Prayer Detail Screen
        composable(
            route = Screen.DailyPrayerDetail.route,
            arguments = listOf(
                navArgument("prayerId") {
                    type = NavType.StringType
                    defaultValue = "doa-1"
                }
            )
        ) { backStackEntry ->
            val rawPrayerId = backStackEntry.arguments?.getString("prayerId") ?: "doa-1"
            val viewModel: DailyPrayerDetailViewModel = viewModel(
                key = "daily_prayer_$rawPrayerId",
                factory = DailyPrayerDetailViewModel.Factory(dailyPrayerRepository, preferencesRepository, rawPrayerId)
            )
            DailyPrayerDetailScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() }
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
                onBackClick = { navController.popBackStackSafe() },
                onPrivacyClick = { navController.navigateSafe(Screen.About.route) }
            )
        }

        // Privacy Screen
        composable(Screen.Privacy.route) {
            PrivacyScreen(
                onBackClick = { navController.popBackStackSafe() }
            )
        }

        // About Screen (App + Developer + Attributions + Links)
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStackSafe() }
            )
        }

        // Asmaul Husna Screen (99 Nama Allah Yang Indah)
        composable(Screen.AsmaulHusna.route) {
            val viewModel: AsmaulHusnaViewModel = viewModel(
                factory = AsmaulHusnaViewModel.Factory(asmaulHusnaRepository)
            )
            AsmaulHusnaScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStackSafe() },
                onHomeClick = { navController.navigateToHome() }
            )
        }
    }
}
