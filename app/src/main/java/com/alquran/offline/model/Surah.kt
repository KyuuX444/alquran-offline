package com.alquran.offline.model

data class Surah(
    val id: Int,
    val nameAr: String,
    val nameLatin: String,
    val translationId: String,
    val type: String,
    val totalVerses: Int,
    val juzStart: Int,
    val juzEnd: Int
)
