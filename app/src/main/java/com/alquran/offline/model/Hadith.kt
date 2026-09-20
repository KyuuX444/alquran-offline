package com.alquran.offline.model

data class Hadith(
    val id: Int,
    val kitab: String,
    val nomor: Int,
    val judul: String,
    val sumber: String,
    val teksAr: String,
    val teksId: String,
    val tema: String,
    val isBookmarked: Boolean = false
)
