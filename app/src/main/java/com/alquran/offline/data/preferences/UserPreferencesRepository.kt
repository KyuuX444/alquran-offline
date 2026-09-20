package com.alquran.offline.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alquran.offline.model.LastRead
import com.alquran.offline.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private val safeData: Flow<Preferences> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }

    private object Keys {
        val ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size")
        val TRANSLATION_FONT_SIZE = floatPreferencesKey("translation_font_size")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val THEME_MODE = stringPreferencesKey("theme_mode")

        val LAST_READ_SURAH_ID = intPreferencesKey("last_read_surah_id")
        val LAST_READ_SURAH_NAME = stringPreferencesKey("last_read_surah_name")
        val LAST_READ_VERSE_ID = intPreferencesKey("last_read_verse_id")
        val LAST_READ_TIMESTAMP = longPreferencesKey("last_read_timestamp")

        // Notification Settings
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        val LAST_NOTIFIED_DAY_OF_YEAR = intPreferencesKey("last_notified_day_of_year")
        val LAST_NOTIFIED_HADITH_INDEX = intPreferencesKey("last_notified_hadith_index")
    }

    val arabicFontSize: Flow<Float> = safeData.map { preferences ->
        preferences[Keys.ARABIC_FONT_SIZE] ?: 28f
    }

    val translationFontSize: Flow<Float> = safeData.map { preferences ->
        preferences[Keys.TRANSLATION_FONT_SIZE] ?: 15f
    }

    val showTranslation: Flow<Boolean> = safeData.map { preferences ->
        preferences[Keys.SHOW_TRANSLATION] ?: true
    }

    val themeMode: Flow<ThemeMode> = safeData.map { preferences ->
        val mode = preferences[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(mode)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val lastRead: Flow<LastRead> = safeData.map { preferences ->
        LastRead(
            surahId = preferences[Keys.LAST_READ_SURAH_ID] ?: 1,
            surahNameLatin = preferences[Keys.LAST_READ_SURAH_NAME] ?: "Al-Fatihah",
            verseId = preferences[Keys.LAST_READ_VERSE_ID] ?: 1,
            timestamp = preferences[Keys.LAST_READ_TIMESTAMP] ?: 0L
        )
    }

    val notificationEnabled: Flow<Boolean> = safeData.map { preferences ->
        preferences[Keys.NOTIFICATION_ENABLED] ?: true
    }

    val notificationHour: Flow<Int> = safeData.map { preferences ->
        preferences[Keys.NOTIFICATION_HOUR] ?: 7
    }

    val notificationMinute: Flow<Int> = safeData.map { preferences ->
        preferences[Keys.NOTIFICATION_MINUTE] ?: 0
    }

    private suspend fun safeEdit(action: suspend (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        try {
            context.dataStore.edit(action)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun setArabicFontSize(size: Float) {
        safeEdit { preferences ->
            preferences[Keys.ARABIC_FONT_SIZE] = size
        }
    }

    suspend fun setTranslationFontSize(size: Float) {
        safeEdit { preferences ->
            preferences[Keys.TRANSLATION_FONT_SIZE] = size
        }
    }

    suspend fun setShowTranslation(show: Boolean) {
        safeEdit { preferences ->
            preferences[Keys.SHOW_TRANSLATION] = show
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        safeEdit { preferences ->
            preferences[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun saveLastRead(surahId: Int, surahName: String, verseId: Int) {
        safeEdit { preferences ->
            preferences[Keys.LAST_READ_SURAH_ID] = surahId
            preferences[Keys.LAST_READ_SURAH_NAME] = surahName
            preferences[Keys.LAST_READ_VERSE_ID] = verseId
            preferences[Keys.LAST_READ_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun resetLastRead() {
        safeEdit { preferences ->
            preferences[Keys.LAST_READ_SURAH_ID] = 1
            preferences[Keys.LAST_READ_SURAH_NAME] = "Al-Fatihah"
            preferences[Keys.LAST_READ_VERSE_ID] = 1
            preferences[Keys.LAST_READ_TIMESTAMP] = 0L
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        safeEdit { preferences ->
            preferences[Keys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        safeEdit { preferences ->
            preferences[Keys.NOTIFICATION_HOUR] = hour
            preferences[Keys.NOTIFICATION_MINUTE] = minute
        }
    }

    suspend fun getNextHadithIndex(totalHadiths: Int): Int {
        var nextIdx = 0
        safeEdit { preferences ->
            val lastIdx = preferences[Keys.LAST_NOTIFIED_HADITH_INDEX] ?: 0
            nextIdx = (lastIdx + 1) % totalHadiths
            preferences[Keys.LAST_NOTIFIED_HADITH_INDEX] = nextIdx
        }
        return nextIdx
    }
}
