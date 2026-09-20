package com.alquran.offline.model

data class PrayerReading(
    val id: Int,
    val title: String,
    val category: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val source: String,
    val explanation: String = ""
)
