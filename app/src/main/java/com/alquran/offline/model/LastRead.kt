package com.alquran.offline.model

data class LastRead(
    val surahId: Int = 1,
    val surahNameLatin: String = "Al-Fatihah",
    val verseId: Int = 1,
    val timestamp: Long = 0L
)
