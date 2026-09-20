package com.alquran.offline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alquran.offline.data.local.entity.HadithBookmarkEntity
import com.alquran.offline.data.local.entity.HadithEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    @Query("SELECT * FROM hadiths ORDER BY nomor ASC")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths ORDER BY nomor ASC")
    suspend fun getAllHadithsList(): List<HadithEntity>

    @Query("SELECT * FROM hadiths WHERE kitab = :kitab ORDER BY nomor ASC")
    fun getHadithsByKitab(kitab: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE kitab = :kitab ORDER BY nomor ASC")
    suspend fun getHadithsByKitabList(kitab: String): List<HadithEntity>

    @Query("SELECT DISTINCT kitab FROM hadiths ORDER BY min(id) ASC")
    fun getAvailableKitabs(): Flow<List<String>>

    @Query("SELECT DISTINCT kitab FROM hadiths ORDER BY min(id) ASC")
    suspend fun getAvailableKitabsList(): List<String>

    @Query("SELECT * FROM hadiths WHERE id = :id LIMIT 1")
    suspend fun getHadithById(id: Int): HadithEntity?

    @Query("SELECT * FROM hadiths WHERE nomor = :nomor LIMIT 1")
    suspend fun getHadithByNumber(nomor: Int): HadithEntity?

    @Query("""
        SELECT * FROM hadiths
        WHERE judul LIKE '%' || :query || '%'
           OR sumber LIKE '%' || :query || '%'
           OR teks_id LIKE '%' || :query || '%'
           OR teks_ar LIKE '%' || :query || '%'
           OR CAST(nomor AS TEXT) = :query
        ORDER BY nomor ASC
    """)
    suspend fun searchHadiths(query: String): List<HadithEntity>

    @Query("SELECT COUNT(*) FROM hadiths")
    suspend fun getHadithCount(): Int

    // Bookmarks
    @Query("SELECT EXISTS(SELECT 1 FROM hadith_bookmarks WHERE hadith_id = :hadithId LIMIT 1)")
    suspend fun isBookmarked(hadithId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM hadith_bookmarks WHERE hadith_id = :hadithId LIMIT 1)")
    fun isBookmarkedFlow(hadithId: Int): Flow<Boolean>

    @Query("SELECT * FROM hadith_bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<HadithBookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: HadithBookmarkEntity): Long

    @Query("DELETE FROM hadith_bookmarks WHERE hadith_id = :hadithId")
    suspend fun deleteBookmark(hadithId: Int)

    @Query("SELECT hadith_id FROM hadith_bookmarks")
    suspend fun getAllBookmarkedHadithIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hadiths: List<HadithEntity>)
}
