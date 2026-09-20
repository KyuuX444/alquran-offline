package com.alquran.offline.model

/**
 * Domain model representing an authentic daily prayer (Doa Harian)
 * sourced from the Holy Qur'an and verified Sunnah / Hadith collections.
 */
data class DailyPrayer(
    val id: String,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String?,
    val translation: String?,
    val source: String?,
    val reference: String?
)
