package com.alquran.offline.model

data class JuzInfo(
    val juzNumber: Int,
    val startSurahId: Int,
    val startSurahName: String,
    val startVerse: Int,
    val endSurahId: Int,
    val endSurahName: String,
    val endVerse: Int
)
