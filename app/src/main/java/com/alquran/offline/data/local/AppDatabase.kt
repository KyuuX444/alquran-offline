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
import org.json.JSONArray
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader

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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                ensurePrepackagedDatabaseIntegrity(appContext)

                val instance = Room.databaseBuilder(
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
                            ensureTablesAndIndices(db)
                            ensureHadithsPopulated(appContext, db)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Validates the integrity, schema identity hash, and content completeness of
         * [DATABASE_NAME] before Room initializes. If the database file is missing,
         * corrupt, or contains an outdated Room identity hash / schema (e.g. from a prior
         * app version installation), user bookmarks are backed up and the pristine prepackaged
         * database is copied from assets to ensure instantaneous, zero-delay startup without
         * infinite loading states.
         */
        private fun ensurePrepackagedDatabaseIntegrity(context: Context) {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if (!dbFile.exists()) {
                try {
                    copyAssetDatabase(context, dbFile)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                return
            }

            var needsRecreation = false
            val savedVerseBookmarks = mutableListOf<Triple<Int, Int, Long>>()
            val savedHadithBookmarks = mutableListOf<Pair<Int, Long>>()

            try {
                SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                ).use { db ->
                    // 1. SQLite integrity check
                    val integrityCursor = db.rawQuery("PRAGMA integrity_check", null)
                    val integrityOk = integrityCursor.use {
                        if (it.moveToFirst()) it.getString(0).equals("ok", ignoreCase = true) else false
                    }
                    if (!integrityOk) {
                        needsRecreation = true
                    }

                    // 2. Room identity hash verification
                    if (!needsRecreation) {
                        try {
                            val hashCursor = db.rawQuery(
                                "SELECT identity_hash FROM room_master_table WHERE id = 42 LIMIT 1",
                                null
                            )
                            val hash = hashCursor.use {
                                if (it.moveToFirst()) it.getString(0) else ""
                            }
                            if (hash != EXPECTED_ROOM_IDENTITY_HASH) {
                                needsRecreation = true
                            }
                        } catch (e: Throwable) {
                            needsRecreation = true
                        }
                    }

                    // 3. Completeness verification (114 Surahs, 6236 Ayahs, 38144 Hadiths)
                    if (!needsRecreation) {
                        try {
                            val surahCount = db.compileStatement("SELECT COUNT(*) FROM surahs").simpleQueryForLong()
                            val ayahCount = db.compileStatement("SELECT COUNT(*) FROM ayahs").simpleQueryForLong()
                            val hadithCount = db.compileStatement("SELECT COUNT(*) FROM hadiths").simpleQueryForLong()
                            if (surahCount != 114L || ayahCount != 6236L || hadithCount != 38144L) {
                                needsRecreation = true
                            }
                        } catch (e: Throwable) {
                            needsRecreation = true
                        }
                    }

                    // If recreation is needed, back up any existing bookmarks before deletion
                    if (needsRecreation) {
                        try {
                            db.rawQuery("SELECT surah_id, verse_id, created_at FROM bookmarks", null).use { cursor ->
                                while (cursor.moveToNext()) {
                                    savedVerseBookmarks.add(
                                        Triple(cursor.getInt(0), cursor.getInt(1), cursor.getLong(2))
                                    )
                                }
                            }
                        } catch (ignored: Throwable) {
                        }

                        try {
                            db.rawQuery("SELECT hadith_id, created_at FROM hadith_bookmarks", null).use { cursor ->
                                while (cursor.moveToNext()) {
                                    savedHadithBookmarks.add(
                                        Pair(cursor.getInt(0), cursor.getLong(1))
                                    )
                                }
                            }
                        } catch (ignored: Throwable) {
                        }
                    }
                }
            } catch (e: Throwable) {
                // If database failed to open or is malformed
                needsRecreation = true
            }

            if (needsRecreation) {
                try {
                    context.deleteDatabase(DATABASE_NAME)
                    val walFile = File(dbFile.path + "-wal")
                    if (walFile.exists()) walFile.delete()
                    val shmFile = File(dbFile.path + "-shm")
                    if (shmFile.exists()) shmFile.delete()
                    val journalFile = File(dbFile.path + "-journal")
                    if (journalFile.exists()) journalFile.delete()

                    copyAssetDatabase(context, dbFile)

                    if (savedVerseBookmarks.isNotEmpty() || savedHadithBookmarks.isNotEmpty()) {
                        restoreBookmarks(dbFile, savedVerseBookmarks, savedHadithBookmarks)
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        private fun copyAssetDatabase(context: Context, destFile: File) {
            destFile.parentFile?.mkdirs()
            context.assets.open(ASSET_DB_NAME).use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(8192)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                    output.flush()
                }
            }
        }

        private fun restoreBookmarks(
            dbFile: File,
            verseBookmarks: List<Triple<Int, Int, Long>>,
            hadithBookmarks: List<Pair<Int, Long>>
        ) {
            try {
                SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                ).use { db ->
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
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        private fun ensureTablesAndIndices(db: SupportSQLiteDatabase) {
            try {
                // Ensure bookmarks table exists
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        surah_id INTEGER NOT NULL,
                        verse_id INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        note TEXT NOT NULL DEFAULT ''
                    );
                """.trimIndent())

                // Ensure hadiths table exists
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

                // Ensure hadith_bookmarks table exists
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS hadith_bookmarks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        hadith_id INTEGER NOT NULL,
                        created_at INTEGER NOT NULL,
                        note TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY (hadith_id) REFERENCES hadiths(id) ON DELETE CASCADE
                    );
                """.trimIndent())

                // Ensure all expected indices exist
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

        private fun ensureHadithsPopulated(context: Context, db: SupportSQLiteDatabase) {
            try {
                var hadithCount = 0
                val cursor = db.query("SELECT COUNT(*) FROM hadiths")
                if (cursor.moveToFirst()) {
                    hadithCount = cursor.getInt(0)
                }
                cursor.close()

                if (hadithCount == 0) {
                    val inputStream = context.assets.open("hadith/hadiths.json")
                    val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
                    val jsonContent = reader.use { it.readText() }
                    val jsonArray = JSONArray(jsonContent)

                    db.beginTransaction()
                    try {
                        val stmt = db.compileStatement("""
                            INSERT OR REPLACE INTO hadiths (id, kitab, nomor, judul, sumber, teks_ar, teks_id, tema)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """.trimIndent())

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            stmt.clearBindings()
                            stmt.bindLong(1, obj.getLong("id"))
                            stmt.bindString(2, obj.getString("kitab"))
                            stmt.bindLong(3, obj.getLong("nomor"))
                            stmt.bindString(4, obj.getString("judul"))
                            stmt.bindString(5, obj.getString("sumber"))
                            stmt.bindString(6, obj.getString("teks_ar"))
                            stmt.bindString(7, obj.getString("teks_id"))
                            stmt.bindString(8, obj.getString("tema"))
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
    }
}
