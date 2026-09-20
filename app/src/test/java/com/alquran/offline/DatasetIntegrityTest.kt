package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.sql.DriverManager

class DatasetIntegrityTest {

    private fun getDbFile(): File {
        val pathCandidates = listOf(
            File("src/main/assets/quran.db"),
            File("app/src/main/assets/quran.db"),
            File("/root/Alquran/app/src/main/assets/quran.db")
        )
        val file = pathCandidates.firstOrNull { it.exists() }
        assertNotNull("quran.db must exist in assets", file)
        return file!!
    }

    @Test
    fun testSurahCountIs114() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT COUNT(*) FROM surahs")
            assertTrue(rs.next())
            val count = rs.getInt(1)
            assertEquals("Total surahs must be 114", 114, count)
        }
    }

    @Test
    fun testAyahCountIs6236() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT COUNT(*) FROM ayahs")
            assertTrue(rs.next())
            val count = rs.getInt(1)
            assertEquals("Total ayahs must be exactly 6,236", 6236, count)
        }
    }

    @Test
    fun testFirstAndLastSurahs() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            
            // First: Al-Fatihah
            val rs1 = statement.executeQuery("SELECT name_latin, total_verses FROM surahs WHERE id = 1")
            assertTrue(rs1.next())
            assertEquals("Al-Fatihah", rs1.getString("name_latin"))
            assertEquals(7, rs1.getInt("total_verses"))

            // Last: An-Nas
            val rs2 = statement.executeQuery("SELECT name_latin, total_verses FROM surahs WHERE id = 114")
            assertTrue(rs2.next())
            assertEquals("An-Nas", rs2.getString("name_latin"))
            assertEquals(6, rs2.getInt("total_verses"))
        }
    }

    @Test
    fun testAll30JuzCovered() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT COUNT(DISTINCT juz_id) FROM ayahs")
            assertTrue(rs.next())
            val count = rs.getInt(1)
            assertEquals("All 30 juz must be present", 30, count)
        }
    }
}
