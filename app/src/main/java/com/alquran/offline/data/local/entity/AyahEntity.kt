package com.alquran.offline.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alquran.offline.model.Ayah

@Entity(
    tableName = "ayahs",
    foreignKeys = [
        ForeignKey(
            entity = SurahEntity::class,
            parentColumns = ["id"],
            childColumns = ["surah_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["surah_id", "verse_id"], name = "idx_ayahs_surah_verse"),
        Index(value = ["juz_id"], name = "idx_ayahs_juz")
    ]
)
data class AyahEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "surah_id") val surahId: Int,
    @ColumnInfo(name = "verse_id") val verseId: Int,
    @ColumnInfo(name = "juz_id") val juzId: Int,
    @ColumnInfo(name = "text_ar") val textAr: String,
    @ColumnInfo(name = "text_id") val textId: String,
    @ColumnInfo(name = "transliteration") val transliteration: String
) {
    fun toDomain(isBookmarked: Boolean = false): Ayah = Ayah(
        id = id,
        surahId = surahId,
        verseId = verseId,
        juzId = juzId,
        textAr = textAr,
        textId = textId,
        transliteration = transliteration,
        isBookmarked = isBookmarked
    )
}
