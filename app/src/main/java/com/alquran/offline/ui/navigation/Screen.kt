package com.alquran.offline.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SurahList : Screen("surah_list")
    object JuzList : Screen("juz_list")
    object Bookmark : Screen("bookmark")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Privacy : Screen("privacy")
    object About : Screen("about")
    object PrayerList : Screen("prayer_list")

    object PrayerDetail : Screen("prayer_detail/{prayerId}") {
        fun createRoute(prayerId: Int): String {
            return "prayer_detail/$prayerId"
        }
    }

    object HadithDetail : Screen("hadith_detail/{hadithId}") {
        fun createRoute(hadithId: Int): String {
            return "hadith_detail/$hadithId"
        }
    }

    object HadithList : Screen("hadith_list?initialId={initialId}") {
        fun createRoute(initialId: Int = 0): String {
            return "hadith_list?initialId=$initialId"
        }
    }

    object DailyPrayerList : Screen("daily_prayer_list")

    object DailyPrayerDetail : Screen("daily_prayer_detail/{prayerId}") {
        fun createRoute(prayerId: String): String {
            return "daily_prayer_detail/$prayerId"
        }
    }

    object Reader : Screen("reader/{surahId}?targetVerse={targetVerse}") {
        fun createRoute(surahId: Int, targetVerse: Int = 1): String {
            return "reader/$surahId?targetVerse=$targetVerse"
        }
    }

    object AsmaulHusna : Screen("asmaul_husna")
}
