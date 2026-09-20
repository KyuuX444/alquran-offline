package com.alquran.offline.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alquran.offline.data.local.entity.AyahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surah_id = :surahId ORDER BY verse_id ASC")
    fun getAyahsBySurah(surahId: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE surah_id = :surahId ORDER BY verse_id ASC")
    suspend fun getAyahsBySurahList(surahId: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE juz_id = :juzId ORDER BY surah_id ASC, verse_id ASC")
    fun getAyahsByJuz(juzId: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE surah_id = :surahId AND verse_id = :verseId LIMIT 1")
    suspend fun getAyah(surahId: Int, verseId: Int): AyahEntity?

    @Query("""
        SELECT * FROM ayahs 
        WHERE text_id LIKE '%' || :query || '%' 
           OR text_ar LIKE '%' || :query || '%'
           OR transliteration LIKE '%' || :query || '%'
        LIMIT :limit
    """)
    suspend fun searchAyahs(query: String, limit: Int = 100): List<AyahEntity>

    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getTotalAyahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ayahs: List<AyahEntity>)
}
