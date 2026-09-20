package com.alquran.offline.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alquran.offline.model.Surah

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name_ar") val nameAr: String,
    @ColumnInfo(name = "name_latin") val nameLatin: String,
    @ColumnInfo(name = "translation_id") val translationId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "total_verses") val totalVerses: Int,
    @ColumnInfo(name = "juz_start") val juzStart: Int,
    @ColumnInfo(name = "juz_end") val juzEnd: Int
) {
    fun toDomain(): Surah = Surah(
        id = id,
        nameAr = nameAr,
        nameLatin = nameLatin,
        translationId = translationId,
        type = type,
        totalVerses = totalVerses,
        juzStart = juzStart,
        juzEnd = juzEnd
    )
}
