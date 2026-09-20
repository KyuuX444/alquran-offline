package com.alquran.offline.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alquran.offline.model.Hadith

@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "kitab") val kitab: String,
    @ColumnInfo(name = "nomor") val nomor: Int,
    @ColumnInfo(name = "judul") val judul: String,
    @ColumnInfo(name = "sumber") val sumber: String,
    @ColumnInfo(name = "teks_ar") val teksAr: String,
    @ColumnInfo(name = "teks_id") val teksId: String,
    @ColumnInfo(name = "tema") val tema: String
) {
    fun toDomain(isBookmarked: Boolean = false): Hadith = Hadith(
        id = id,
        kitab = kitab,
        nomor = nomor,
        judul = judul,
        sumber = sumber,
        teksAr = teksAr,
        teksId = teksId,
        tema = tema,
        isBookmarked = isBookmarked
    )
}
