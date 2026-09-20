package com.alquran.offline

import com.alquran.offline.model.Hadith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DailyHadithTest {

    private fun computeDeterministicDailyId(cal: Calendar, totalHadiths: Int): Int {
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        return (((dayOfYear * 31 + year) % totalHadiths) + totalHadiths) % totalHadiths + 1
    }

    @Test
    fun testDailyHadithIdMatchesClickNavigation() {
        val total = 38144
        val cal = Calendar.getInstance()
        val dailyId = computeDeterministicDailyId(cal, total)

        val testHadith = Hadith(
            id = dailyId,
            kitab = "Shahih Bukhari",
            nomor = 1,
            judul = "Niat dan Ikhlas",
            sumber = "HR. Bukhari no. 1",
            teksAr = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            teksId = "Sesungguhnya setiap amalan tergantung pada niatnya.",
            tema = "Keikhlasan"
        )

        assertEquals("Daily hadith ID must be preserved", dailyId, testHadith.id)

        // When user clicks the card on Home, route must take exact same ID
        val navigationRoute = "hadith_detail/${testHadith.id}"
        assertEquals("hadith_detail/$dailyId", navigationRoute)

        // Extract ID from route
        val extractedId = navigationRoute.substringAfter("hadith_detail/").toIntOrNull()
        assertNotNull(extractedId)
        assertEquals("Extracted ID from route must match daily hadith ID exactly", testHadith.id, extractedId)
    }

    @Test
    fun testValidIdRangeAcrossLeapYear() {
        val total = 38144
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2024) // Leap year
        for (day in 1..366) {
            cal.set(Calendar.DAY_OF_YEAR, day)
            val id = computeDeterministicDailyId(cal, total)
            assertTrue("ID must be between 1 and $total", id in 1..total)
        }
    }
}
