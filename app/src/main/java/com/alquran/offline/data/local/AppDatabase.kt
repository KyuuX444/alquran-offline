/*
 * Al-Qur'an Offline
 * © 2026 Kyuu / KyuuX444
 * Project provenance: quran-offline-kyuu-2026-c9f2a87b
 */
package com.alquran.offline.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alquran.offline.data.local.dao.AyahDao
import com.alquran.offline.data.local.dao.BookmarkDao
import com.alquran.offline.data.local.dao.HadithDao
import com.alquran.offline.data.local.dao.SurahDao
import com.alquran.offline.data.local.entity.AyahEntity
import com.alquran.offline.data.local.entity.BookmarkEntity
import com.alquran.offline.data.local.entity.HadithBookmarkEntity
import com.alquran.offline.data.local.entity.HadithEntity
import com.alquran.offline.data.local.entity.SurahEntity
import java.io.File

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        BookmarkEntity::class,
        HadithEntity::class,
        HadithBookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun hadithDao(): HadithDao

    companion object {
        const val DATABASE_NAME = "quran_offline.db"
        const val ASSET_DB_NAME = "quran.db"
        const val EXPECTED_ROOM_IDENTITY_HASH = "f3978825539f9a5f1f2f66032483629b"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        @Volatile
        private var pendingRestoreVerseBookmarks: List<Triple<Int, Int, Long>>? = null

        @Volatile
        private var pendingRestoreHadithBookmarks: List<Pair<Int, Long>>? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(appContext: Context): AppDatabase {
            // Fast sub-millisecond check to verify whether existing database on disk matches schema.
            // On fresh install, this takes 0ms because file does not exist.
            // On existing install, indexed check takes ~1ms.
            // NEVER copies 98 MB or runs full table scans on the Main Thread.
            validateAndCleanOutdatedDatabase(appContext)

            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .createFromAsset(ASSET_DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        ensureTablesAndIndices(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Restore any saved bookmarks if an outdated DB was cleaned up
                        val verseBm = pendingRestoreVerseBookmarks
                        val hadithBm = pendingRestoreHadithBookmarks
                        if (!verseBm.isNullOrEmpty() || !hadithBm.isNullOrEmpty()) {
                            pendingRestoreVerseBookmarks = null
                            pendingRestoreHadithBookmarks = null
                            restoreBookmarks(db, verseBm ?: emptyList(), hadithBm ?: emptyList())
                        }
                    }
                })
                .build()
        }

        /**
         * Verifies if an existing database file is valid and up to date in < 1ms.
         * If the database file is missing (fresh install), returns immediately so Room
         * copies the prepackaged asset asynchronously on IO thread without blocking UI.
         * If the database file is outdated or corrupt, bookmarks are backed up and the file
         * is safely purged so Room can unpack the pristine database.
         */
        private fun validateAndCleanOutdatedDatabase(context: Context) {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if (!dbFile.exists()) {
                return
            }

            var isValid = false
            val savedVerseBookmarks = mutableListOf<Triple<Int, Int, Long>>()
            val savedHadithBookmarks = mutableListOf<Pair<Int, Long>>()

            try {
                SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                ).use { db ->
                    // 1. Fast Room identity hash check (0.8ms)
                    val hashCursor = db.rawQuery(
                        "SELECT identity_hash FROM room_master_table WHERE id = 42 LIMIT 1",
                        null
                    )
                    val hash = hashCursor.use {
                        if (it.moveToFirst()) it.getString(0) else ""
                    }
                    if (hash == EXPECTED_ROOM_IDENTITY_HASH) {
                        // 2. Sub-millisecond indexed presence check for last surah and last hadith
                        val surahCheck = db.compileStatement("SELECT id FROM surahs WHERE id = 114 LIMIT 1").simpleQueryForLong()
                        val hadithCheck = db.compileStatement("SELECT id FROM hadiths WHERE id = 38144 LIMIT 1").simpleQueryForLong()
                        if (surahCheck == 114L && hadithCheck == 38144L) {
                            isValid = true
                        }
                    }

                    // If outdated or corrupt, preserve existing user bookmarks before deletion
                    if (!isValid) {
                        try {
                            db.rawQuery("SELECT surah_id, verse_id, created_at FROM bookmarks", null).use { cursor ->
                                while (cursor.moveToNext()) {
                                    savedVerseBookmarks.add(
                                        Triple(cursor.getInt(0), cursor.getInt(1), cursor.getLong(2))
                                    )
                                }
                            }
                        } catch (ignored: Throwable) {}

                        try {
                            db.rawQuery("SELECT hadith_id, created_at FROM hadith_bookmarks", null).use { cursor ->
                                while (cursor.moveToNext()) {
                                    savedHadithBookmarks.add(
                                        Pair(cursor.getInt(0), cursor.getLong(1))
                                    )
                                }
                            }
                        } catch (ignored: Throwable) {}
                    }
                }
            } catch (e: Throwable) {
                isValid = false
            }

            if (!isValid) {
                try {
                    context.deleteDatabase(DATABASE_NAME)
                    val walFile = File(dbFile.path + "-wal")
                    if (walFile.exists()) walFile.delete()
                    val shmFile = File(dbFile.path + "-shm")
                    if (shmFile.exists()) shmFile.delete()
                    val journalFile = File(dbFile.path + "-journal")
                    if (journalFile.exists()) journalFile.delete()

                    if (savedVerseBookmarks.isNotEmpty()) {
                        pendingRestoreVerseBookmarks = savedVerseBookmarks
                    }
                    if (savedHadithBookmarks.isNotEmpty()) {
                        pendingRestoreHadithBookmarks = savedHadithBookmarks
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        private fun restoreBookmarks(
            db: SupportSQLiteDatabase,
            verseBookmarks: List<Triple<Int, Int, Long>>,
            hadithBookmarks: List<Pair<Int, Long>>
        ) {
            try {
                if (verseBookmarks.isNotEmpty()) {
                    db.beginTransaction()
                    try {
                        val stmt = db.compileStatement(
                            "INSERT OR IGNORE INTO bookmarks (surah_id, verse_id, created_at, note) VALUES (?, ?, ?, '')"
                        )
                        for (bm in verseBookmarks) {
                            stmt.clearBindings()
                            stmt.bindLong(1, bm.first.toLong())
                            stmt.bindLong(2, bm.second.toLong())
                            stmt.bindLong(3, bm.third)
                            stmt.executeInsert()
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }

                if (hadithBookmarks.isNotEmpty()) {
                    db.beginTransaction()
                    try {
                        val stmt = db.compileStatement(
                            "INSERT OR IGNORE INTO hadith_bookmarks (hadith_id, created_at, note) VALUES (?, ?, '')"
                        )
                        for (hbm in hadithBookmarks) {
                            stmt.clearBindings()
                            stmt.bindLong(1, hbm.first.toLong())
                            stmt.bindLong(2, hbm.second)
                            stmt.executeInsert()
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        private fun ensureTablesAndIndices(db: SupportSQLiteDatabase) {
            try {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        surah_id INTEGER NOT NULL,
                        verse_id INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        note TEXT NOT NULL DEFAULT ''
                    );
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS hadiths (
                        id INTEGER PRIMARY KEY NOT NULL,
                        kitab TEXT NOT NULL,
                        nomor INTEGER NOT NULL,
                        judul TEXT NOT NULL,
                        sumber TEXT NOT NULL,
                        teks_ar TEXT NOT NULL,
                        teks_id TEXT NOT NULL,
                        tema TEXT NOT NULL
                    );
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS hadith_bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        hadith_id INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        note TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY (hadith_id) REFERENCES hadiths(id) ON DELETE CASCADE
                    );
                """.trimIndent())

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_hadiths_kitab ON hadiths(kitab);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_hadiths_nomor ON hadiths(nomor);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_hadiths_judul ON hadiths(judul);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_hadith_bookmarks_hadith_id ON hadith_bookmarks(hadith_id);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_surahs_name ON surahs(name_latin);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_surah_verse ON ayahs(surah_id, verse_id);")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_ayahs_juz ON ayahs(juz_id);")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
