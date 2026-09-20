package com.alquran.offline.model

/**
 * Domain model representing one of the 99 Beautiful Names of Allah (Asmaul Husna).
 */
data class AsmaulHusna(
    val id: Int,
    val nameAr: String,
    val nameLatin: String,
    val meaningId: String
)
