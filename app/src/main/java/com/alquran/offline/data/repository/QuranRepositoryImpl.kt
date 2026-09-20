/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
package com.alquran.offline.data.repository

import com.alquran.offline.data.local.AppDatabase
import com.alquran.offline.data.local.entity.BookmarkEntity
import com.alquran.offline.data.local.entity.HadithBookmarkEntity
import com.alquran.offline.data.preferences.UserPreferencesRepository
import com.alquran.offline.model.Ayah
import com.alquran.offline.model.Bookmark
import com.alquran.offline.model.Hadith
import com.alquran.offline.model.JuzInfo
import com.alquran.offline.model.LastRead
import com.alquran.offline.model.SearchResult
import com.alquran.offline.model.Surah
import com.alquran.offline.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar

class QuranRepositoryImpl(
    private val database: AppDatabase,
    private val preferencesRepository: UserPreferencesRepository
) : QuranRepository {

    private val surahDao = database.surahDao()
    private val ayahDao = database.ayahDao()
    private val bookmarkDao = database.bookmarkDao()
    private val hadithDao = database.hadithDao()

    override fun getAllSurahs(): Flow<List<Surah>> {
        return surahDao.getAllSurahs()
            .map { list -> list.map { it.toDomain() } }
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getSurahById(id: Int): Surah? = withContext(Dispatchers.IO) {
        try {
            val safeId = id.coerceIn(1, 114)
            surahDao.getSurahById(safeId)?.toDomain()
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    override fun getAyahsBySurah(surahId: Int): Flow<List<Ayah>> {
        val safeSurahId = surahId.coerceIn(1, 114)
        return ayahDao.getAyahsBySurah(safeSurahId).map { list ->
            val bookmarkedSet = try {
                bookmarkDao.getBookmarkedVerseIdsForSurah(safeSurahId).toHashSet()
            } catch (e: Throwable) {
                emptySet<Int>()
            }
            list.map { entity ->
                entity.toDomain(isBookmarked = bookmarkedSet.contains(entity.verseId))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }

    override fun getAyahsByJuz(juzId: Int): Flow<List<Ayah>> {
        val safeJuzId = juzId.coerceIn(1, 30)
        return ayahDao.getAyahsByJuz(safeJuzId).map { list ->
            val bookmarkKeys = try {
                bookmarkDao.getAllBookmarkKeys().toHashSet()
            } catch (e: Throwable) {
                emptySet<String>()
            }
            list.map { entity ->
                val key = "${entity.surahId}_${entity.verseId}"
                entity.toDomain(isBookmarked = bookmarkKeys.contains(key))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }

    override fun getAllJuz(): List<JuzInfo> {
        val boundaries = listOf(
            Triple(1, Pair(1, 1), Pair(2, 141)),
            Triple(2, Pair(2, 142), Pair(2, 252)),
            Triple(3, Pair(2, 253), Pair(3, 92)),
            Triple(4, Pair(3, 93), Pair(4, 23)),
            Triple(5, Pair(4, 24), Pair(4, 147)),
            Triple(6, Pair(4, 148), Pair(5, 81)),
            Triple(7, Pair(5, 82), Pair(6, 110)),
            Triple(8, Pair(6, 111), Pair(7, 87)),
            Triple(9, Pair(7, 88), Pair(8, 40)),
            Triple(10, Pair(8, 41), Pair(9, 92)),
            Triple(11, Pair(9, 93), Pair(11, 5)),
            Triple(12, Pair(11, 6), Pair(12, 52)),
            Triple(13, Pair(12, 53), Pair(14, 52)),
            Triple(14, Pair(15, 1), Pair(16, 128)),
            Triple(15, Pair(17, 1), Pair(18, 74)),
            Triple(16, Pair(18, 75), Pair(20, 135)),
            Triple(17, Pair(21, 1), Pair(22, 78)),
            Triple(18, Pair(23, 1), Pair(25, 20)),
            Triple(19, Pair(25, 21), Pair(27, 55)),
            Triple(20, Pair(27, 56), Pair(29, 45)),
            Triple(21, Pair(29, 46), Pair(33, 30)),
            Triple(22, Pair(33, 31), Pair(36, 27)),
            Triple(23, Pair(36, 28), Pair(39, 31)),
            Triple(24, Pair(39, 32), Pair(41, 46)),
            Triple(25, Pair(41, 47), Pair(45, 37)),
            Triple(26, Pair(46, 1), Pair(51, 30)),
            Triple(27, Pair(51, 31), Pair(57, 29)),
            Triple(28, Pair(58, 1), Pair(66, 12)),
            Triple(29, Pair(67, 1), Pair(77, 50)),
            Triple(30, Pair(78, 1), Pair(114, 6))
        )

        val surahNames = mapOf(
            1 to "Al-Fatihah", 2 to "Al-Baqarah", 3 to "Ali 'Imran", 4 to "An-Nisa'",
            5 to "Al-Ma'idah", 6 to "Al-An'am", 7 to "Al-A'raf", 8 to "Al-Anfal",
            9 to "At-Tawbah", 10 to "Yunus", 11 to "Hud", 12 to "Yusuf",
            13 to "Ar-Ra'd", 14 to "Ibrahim", 15 to "Al-Hijr", 16 to "An-Nahl",
            17 to "Al-Isra'", 18 to "Al-Kahf", 19 to "Maryam", 20 to "Taha",
            21 to "Al-Anbiya'", 22 to "Al-Hajj", 23 to "Al-Mu'minun", 24 to "An-Nur",
            25 to "Al-Furqan", 26 to "Asy-Syu'ara'", 27 to "An-Naml", 28 to "Al-Qasas",
            29 to "Al-'Ankabut", 30 to "Ar-Rum", 31 to "Luqman", 32 to "As-Sajdah",
            33 to "Al-Ahzab", 34 to "Saba'", 35 to "Fatir", 36 to "Yasin",
            37 to "As-Saffat", 38 to "Sad", 39 to "Az-Zumar", 40 to "Ghafir",
            41 to "Fussilat", 42 to "Asy-Syura", 43 to "Az-Zukhruf", 44 to "Ad-Dukhan",
            45 to "Al-Jasiyah", 46 to "Al-Ahqaf", 47 to "Muhammad", 48 to "Al-Fath",
            49 to "Al-Hujurat", 50 to "Qaf", 51 to "Az-Zariyat", 52 to "At-Tur",
            53 to "An-Najm", 54 to "Al-Qamar", 55 to "Ar-Rahman", 56 to "Al-Waqi'ah",
            57 to "Al-Hadid", 58 to "Al-Mujadilah", 59 to "Al-Hasyr", 60 to "Al-Mumtahanah",
            61 to "As-Saff", 62 to "Al-Jumu'ah", 63 to "Al-Munafiqun", 64 to "At-Tagabun",
            65 to "At-Talaq", 66 to "At-Tahrim", 67 to "Al-Mulk", 68 to "Al-Qalam",
            69 to "Al-Haqqah", 70 to "Al-Ma'arij", 71 to "Nuh", 72 to "Al-Jinn",
            73 to "Al-Muzzammil", 74 to "Al-Muddassir", 75 to "Al-Qiyamah", 76 to "Al-Insan",
            77 to "Al-Mursalat", 78 to "An-Naba'", 79 to "An-Nazi'at", 80 to "'Abasa",
            81 to "At-Takwir", 82 to "Al-Infitar", 83 to "Al-Mutaffifin", 84 to "Al-Insyiqaq",
            85 to "Al-Buruj", 86 to "At-Tariq", 87 to "Al-A'la", 88 to "Al-Ghasyiyah",
            89 to "Al-Fajr", 90 to "Al-Balad", 91 to "Asy-Syams", 92 to "Al-Lail",
            93 to "Ad-Duha", 94 to "Asy-Syarh", 95 to "At-Tin", 96 to "Al-'Alaq",
            97 to "Al-Qadr", 98 to "Al-Bayyinah", 99 to "Az-Zalzalah", 100 to "Al-'Adiyat",
            101 to "Al-Qari'ah", 102 to "At-Takasur", 103 to "Al-'Asr", 104 to "Al-Humazah",
            105 to "Al-Fil", 106 to "Quraisy", 107 to "Al-Ma'un", 108 to "Al-Kausar",
            109 to "Al-Kafirun", 110 to "An-Nasr", 111 to "Al-Lahab", 112 to "Al-Ikhlas",
            113 to "Al-Falaq", 114 to "An-Nas"
        )

        return boundaries.map { (juz, start, end) ->
            JuzInfo(
                juzNumber = juz,
                startSurahId = start.first,
                startSurahName = surahNames[start.first] ?: "Surah ${start.first}",
                startVerse = start.second,
                endSurahId = end.first,
                endSurahName = surahNames[end.first] ?: "Surah ${end.first}",
                endVerse = end.second
            )
        }
    }

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { list ->
            list.map { bEntity ->
                val surah = try {
                    surahDao.getSurahById(bEntity.surahId)
                } catch (e: Throwable) {
                    null
                }
                val ayah = try {
                    ayahDao.getAyah(bEntity.surahId, bEntity.verseId)
                } catch (e: Throwable) {
                    null
                }
                bEntity.toDomain(
                    surahName = surah?.nameLatin ?: "Surah ${bEntity.surahId}",
                    textAr = ayah?.textAr ?: "",
                    textId = ayah?.textId ?: ""
                )
            }
        }.catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }

    override fun isBookmarked(surahId: Int, verseId: Int): Flow<Boolean> {
        return bookmarkDao.isBookmarked(surahId, verseId)
            .catch { e ->
                e.printStackTrace()
                emit(false)
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addBookmark(surahId: Int, verseId: Int, note: String): Unit = withContext(Dispatchers.IO) {
        try {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    surahId = surahId,
                    verseId = verseId,
                    createdAt = System.currentTimeMillis(),
                    note = note
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override suspend fun removeBookmark(surahId: Int, verseId: Int) = withContext(Dispatchers.IO) {
        try {
            bookmarkDao.deleteBookmark(surahId, verseId)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override suspend fun removeBookmarkById(id: Long) = withContext(Dispatchers.IO) {
        try {
            bookmarkDao.deleteBookmarkById(id)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return@withContext emptyList()

        val results = mutableListOf<SearchResult>()
        try {
            // 1. Search surahs
            val matchedSurahs = surahDao.searchSurahs(trimmed)
            results.addAll(matchedSurahs.map { SearchResult.SurahMatch(it.toDomain()) })

            // 2. Search ayahs
            val matchedAyahs = ayahDao.searchAyahs(trimmed, limit = 50)
            for (aEntity in matchedAyahs) {
                val surah = surahDao.getSurahById(aEntity.surahId)
                val isBookmarked = try {
                    bookmarkDao.isBookmarkedSync(aEntity.surahId, aEntity.verseId)
                } catch (e: Throwable) {
                    false
                }
                results.add(
                    SearchResult.AyahMatch(
                        ayah = aEntity.toDomain(isBookmarked = isBookmarked),
                        surahNameLatin = surah?.nameLatin ?: "Surah ${aEntity.surahId}"
                    )
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        results
    }

    // Hadiths
    private val fallbackHadith = Hadith(
        id = 1,
        kitab = "Hadits Arba'in An-Nawawi",
        nomor = 1,
        judul = "Niat dan Ikhlas",
        sumber = "HR. Bukhari dan Muslim",
        teksAr = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
        teksId = "Sesungguhnya setiap amalan tergantung pada niatnya.",
        tema = "Keikhlasan",
        isBookmarked = false
    )

    override fun getAllHadiths(): Flow<List<Hadith>> {
        return hadithDao.getAllHadiths().map { list ->
            val bookmarkedIds = try {
                hadithDao.getAllBookmarkedHadithIds().toHashSet()
            } catch (e: Throwable) {
                emptySet<Int>()
            }
            list.map { entity ->
                entity.toDomain(isBookmarked = bookmarkedIds.contains(entity.id))
            }
        }.catch { e ->
            e.printStackTrace()
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getHadithById(id: Int): Hadith? = withContext(Dispatchers.IO) {
        try {
            val entity = hadithDao.getHadithById(id) ?: return@withContext null
            val isBm = try { hadithDao.isBookmarked(entity.id) } catch (e: Throwable) { false }
            entity.toDomain(isBookmarked = isBm)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getHadithByNumber(nomor: Int): Hadith? = withContext(Dispatchers.IO) {
        try {
            val entity = hadithDao.getHadithByNumber(nomor) ?: return@withContext null
            val isBm = try { hadithDao.isBookmarked(entity.id) } catch (e: Throwable) { false }
            entity.toDomain(isBookmarked = isBm)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getTodayHadith(): Hadith = withContext(Dispatchers.IO) {
        try {
            val count = hadithDao.getHadithCount()
            val total = if (count > 0) count else 42
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val targetNumber = ((dayOfYear - 1) % total) + 1
            val entity = hadithDao.getHadithByNumber(targetNumber)
                ?: hadithDao.getHadithById(1)
                ?: return@withContext fallbackHadith
            val isBm = try { hadithDao.isBookmarked(entity.id) } catch (e: Throwable) { false }
            entity.toDomain(isBookmarked = isBm)
        } catch (e: Exception) {
            fallbackHadith
        }
    }

    override suspend fun searchHadiths(query: String): List<Hadith> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return@withContext emptyList()
        try {
            val list = hadithDao.searchHadiths(trimmed)
            val bookmarkedIds = try {
                hadithDao.getAllBookmarkedHadithIds().toHashSet()
            } catch (e: Throwable) {
                emptySet<Int>()
            }
            list.map { entity ->
                entity.toDomain(isBookmarked = bookmarkedIds.contains(entity.id))
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun toggleHadithBookmark(hadithId: Int): Unit = withContext(Dispatchers.IO) {
        try {
            if (hadithDao.isBookmarked(hadithId)) {
                hadithDao.deleteBookmark(hadithId)
            } else {
                hadithDao.insertBookmark(
                    HadithBookmarkEntity(
                        hadithId = hadithId,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun isHadithBookmarked(hadithId: Int): Flow<Boolean> {
        return hadithDao.isBookmarkedFlow(hadithId)
            .catch { e ->
                e.printStackTrace()
                emit(false)
            }
            .flowOn(Dispatchers.IO)
    }

    override val lastRead: Flow<LastRead> = preferencesRepository.lastRead

    override suspend fun saveLastRead(surahId: Int, surahName: String, verseId: Int) {
        preferencesRepository.saveLastRead(surahId, surahName, verseId)
    }

    override suspend fun resetLastRead() {
        preferencesRepository.resetLastRead()
    }

    override val arabicFontSize: Flow<Float> = preferencesRepository.arabicFontSize
    override val translationFontSize: Flow<Float> = preferencesRepository.translationFontSize
    override val showTranslation: Flow<Boolean> = preferencesRepository.showTranslation
    override val themeMode: Flow<ThemeMode> = preferencesRepository.themeMode

    override suspend fun setArabicFontSize(size: Float) {
        preferencesRepository.setArabicFontSize(size)
    }

    override suspend fun setTranslationFontSize(size: Float) {
        preferencesRepository.setTranslationFontSize(size)
    }

    override suspend fun setShowTranslation(show: Boolean) {
        preferencesRepository.setShowTranslation(show)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        preferencesRepository.setThemeMode(mode)
    }

    // Notifications
    override val notificationEnabled: Flow<Boolean> = preferencesRepository.notificationEnabled
    override val notificationHour: Flow<Int> = preferencesRepository.notificationHour
    override val notificationMinute: Flow<Int> = preferencesRepository.notificationMinute

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        preferencesRepository.setNotificationEnabled(enabled)
    }

    override suspend fun setNotificationTime(hour: Int, minute: Int) {
        preferencesRepository.setNotificationTime(hour, minute)
    }
}
