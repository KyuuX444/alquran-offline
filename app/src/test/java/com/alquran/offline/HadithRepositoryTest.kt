package com.alquran.offline

import com.alquran.offline.data.local.entity.HadithEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.sql.DriverManager

class HadithRepositoryTest {

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
    fun testHadithDatasetContains42Hadiths() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT COUNT(*) FROM hadiths")
            assertTrue(rs.next())
            val count = rs.getInt(1)
            assertEquals("Total hadiths in Arba'in collection must be 42", 42, count)
        }
    }

    @Test
    fun testHadithSequentialNumberingAndNonEmptyFields() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val statement = conn.createStatement()
            val rs = statement.executeQuery("SELECT id, nomor, judul, teks_ar, teks_id, sumber FROM hadiths ORDER BY nomor ASC")
            var expectedNumber = 1
            while (rs.next()) {
                val id = rs.getInt("id")
                val nomor = rs.getInt("nomor")
                val judul = rs.getString("judul")
                val teksAr = rs.getString("teks_ar")
                val teksId = rs.getString("teks_id")
                val sumber = rs.getString("sumber")

                assertEquals("Hadith ID should match sequential index", expectedNumber, id)
                assertEquals("Hadith number should be strictly sequential", expectedNumber, nomor)
                assertTrue("Hadith title #$nomor must not be empty", !judul.isNullOrBlank())
                assertTrue("Hadith Arabic text #$nomor must not be empty", !teksAr.isNullOrBlank())
                assertTrue("Hadith Indonesian translation #$nomor must not be empty", !teksId.isNullOrBlank())
                assertTrue("Hadith source #$nomor must not be empty", !sumber.isNullOrBlank())

                expectedNumber++
            }
            assertEquals("All 42 hadiths must be traversed", 43, expectedNumber)
        }
    }

    @Test
    fun testHadithEntityToDomainMapping() {
        val entity = HadithEntity(
            id = 1,
            kitab = "Arba'in An-Nawawi",
            nomor = 1,
            judul = "Niat dan Ikhlas",
            sumber = "HR. Bukhari dan Muslim",
            teksAr = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            teksId = "Sesungguhnya setiap amalan tergantung pada niatnya.",
            tema = "Keikhlasan"
        )
        val domain = entity.toDomain(isBookmarked = true)
        assertEquals(1, domain.id)
        assertEquals(1, domain.nomor)
        assertEquals("Niat dan Ikhlas", domain.judul)
        assertTrue(domain.isBookmarked)
    }

    @Test
    fun testHadithSearchQuery() {
        val dbFile = getDbFile()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"
        DriverManager.getConnection(url).use { conn ->
            val pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM hadiths WHERE LOWER(judul) LIKE ? OR LOWER(teks_id) LIKE ?"
            )
            pstmt.setString(1, "%niat%")
            pstmt.setString(2, "%niat%")
            val rs = pstmt.executeQuery()
            assertTrue(rs.next())
            val count = rs.getInt(1)
            assertTrue("Searching for 'niat' should yield at least 1 match", count >= 1)
        }
    }
}
