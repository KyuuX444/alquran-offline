package com.alquran.offline.model

sealed class SearchResult {
    data class SurahMatch(
        val surah: Surah
    ) : SearchResult()

    data class AyahMatch(
        val ayah: Ayah,
        val surahNameLatin: String
    ) : SearchResult()
}
