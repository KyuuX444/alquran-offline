/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
package com.alquran.offline.data.repository

import com.alquran.offline.model.Ayah
import com.alquran.offline.model.Bookmark
import com.alquran.offline.model.Hadith
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

    // Hadiths
    fun getAllHadiths(): Flow<List<Hadith>>
    suspend fun getHadithById(id: Int): Hadith?
    suspend fun getHadithByNumber(nomor: Int): Hadith?
    suspend fun getTodayHadith(): Hadith
    suspend fun searchHadiths(query: String): List<Hadith>
    suspend fun toggleHadithBookmark(hadithId: Int)
    fun isHadithBookmarked(hadithId: Int): Flow<Boolean>

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

    // Notifications
    val notificationEnabled: Flow<Boolean>
    val notificationHour: Flow<Int>
    val notificationMinute: Flow<Int>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setNotificationTime(hour: Int, minute: Int)
}
