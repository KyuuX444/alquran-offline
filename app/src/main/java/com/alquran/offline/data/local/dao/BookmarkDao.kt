package com.alquran.offline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alquran.offline.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surah_id = :surahId AND verse_id = :verseId LIMIT 1)")
    fun isBookmarked(surahId: Int, verseId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surah_id = :surahId AND verse_id = :verseId LIMIT 1)")
    suspend fun isBookmarkedSync(surahId: Int, verseId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE surah_id = :surahId AND verse_id = :verseId")
    suspend fun deleteBookmark(surahId: Int, verseId: Int)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("SELECT verse_id FROM bookmarks WHERE surah_id = :surahId")
    suspend fun getBookmarkedVerseIdsForSurah(surahId: Int): List<Int>

    @Query("SELECT surah_id || '_' || verse_id FROM bookmarks")
    suspend fun getAllBookmarkKeys(): List<String>
}
