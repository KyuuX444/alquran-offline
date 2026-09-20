package com.alquran.offline.model

data class Ayah(
    val id: Int,
    val surahId: Int,
    val verseId: Int,
    val juzId: Int,
    val textAr: String,
    val textId: String,
    val transliteration: String,
    val isBookmarked: Boolean = false
)
