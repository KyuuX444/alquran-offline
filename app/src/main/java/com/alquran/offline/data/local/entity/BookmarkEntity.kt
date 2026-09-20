package com.alquran.offline.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alquran.offline.model.Bookmark

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "surah_id") val surahId: Int,
    @ColumnInfo(name = "verse_id") val verseId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "note", defaultValue = "''") val note: String = ""
) {
    fun toDomain(surahName: String = "", textAr: String = "", textId: String = ""): Bookmark = Bookmark(
        id = id,
        surahId = surahId,
        surahNameLatin = surahName,
        verseId = verseId,
        textAr = textAr,
        textId = textId,
        createdAt = createdAt,
        note = note
    )
}
