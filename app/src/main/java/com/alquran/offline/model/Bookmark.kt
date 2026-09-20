package com.alquran.offline.model

data class Bookmark(
    val id: Long = 0,
    val surahId: Int,
    val surahNameLatin: String = "",
    val verseId: Int,
    val textAr: String = "",
    val textId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val note: String = ""
)
