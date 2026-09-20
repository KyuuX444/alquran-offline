package com.alquran.offline.data.repository

import com.alquran.offline.model.Ayah
import com.alquran.offline.model.Bookmark
import com.alquran.offline.model.JuzInfo
import com.alquran.offline.model.LastRead
import com.alquran.offline.model.SearchResult
import com.alquran.offline.model.Surah
import com.alquran.offline.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    fun getAllSurahs(): Flow<List<Surah>>
    suspend fun getSurahById(id: Int): Surah?
    fun getAyahsBySurah(surahId: Int): Flow<List<Ayah>>
    fun getAyahsByJuz(juzId: Int): Flow<List<Ayah>>
    fun getAllJuz(): List<JuzInfo>
    
    // Bookmarks
    fun getAllBookmarks(): Flow<List<Bookmark>>
    fun isBookmarked(surahId: Int, verseId: Int): Flow<Boolean>
    suspend fun addBookmark(surahId: Int, verseId: Int, note: String = "")
    suspend fun removeBookmark(surahId: Int, verseId: Int)
    suspend fun removeBookmarkById(id: Long)

    // Search
    suspend fun search(query: String): List<SearchResult>

    // Last Read & Preferences
    val lastRead: Flow<LastRead>
    suspend fun saveLastRead(surahId: Int, surahName: String, verseId: Int)
    suspend fun resetLastRead()

    val arabicFontSize: Flow<Float>
    val translationFontSize: Flow<Float>
    val showTranslation: Flow<Boolean>
    val themeMode: Flow<ThemeMode>

    suspend fun setArabicFontSize(size: Float)
    suspend fun setTranslationFontSize(size: Float)
    suspend fun setShowTranslation(show: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
}
