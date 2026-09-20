package com.alquran.offline.data.local

import android.content.Context
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
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "quran_offline.db"
                )
                    .createFromAsset("quran.db")
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
