package com.alquran.offline.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SurahList : Screen("surah_list")
    object JuzList : Screen("juz_list")
    object Bookmark : Screen("bookmark")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Privacy : Screen("privacy")

    object HadithList : Screen("hadith_list?initialId={initialId}") {
        fun createRoute(initialId: Int = 0): String {
            return "hadith_list?initialId=$initialId"
        }
    }

    object Reader : Screen("reader/{surahId}?targetVerse={targetVerse}") {
        fun createRoute(surahId: Int, targetVerse: Int = 1): String {
            return "reader/$surahId?targetVerse=$targetVerse"
        }
    }
}
