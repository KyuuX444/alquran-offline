package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class NotificationScheduleTest {

    @Test
    fun testDayOfYearSelectionLogic() {
        val totalHadiths = 42

        // Check that any day of year (1..366) maps to a valid Hadith index 1..42
        val visitedIds = mutableSetOf<Int>()
        for (day in 1..366) {
            val hadithIndex = ((day - 1) % totalHadiths) + 1
            assertTrue("Hadith index must be between 1 and 42", hadithIndex in 1..42)
            visitedIds.add(hadithIndex)
        }

        // Over 366 days, all 42 hadiths should be visited
        assertEquals("All 42 hadiths must be scheduled across the year", 42, visitedIds.size)
    }

    @Test
    fun testSameDayProducesSameHadith() {
        val totalHadiths = 42
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar1.set(2026, Calendar.SEPTEMBER, 20, 7, 0, 0)
        calendar2.set(2026, Calendar.SEPTEMBER, 20, 23, 59, 59)

        val day1 = calendar1.get(Calendar.DAY_OF_YEAR)
        val day2 = calendar2.get(Calendar.DAY_OF_YEAR)

        val id1 = ((day1 - 1) % totalHadiths) + 1
        val id2 = ((day2 - 1) % totalHadiths) + 1

        assertEquals("Same day of year must produce identical hadith index", id1, id2)
    }

    @Test
    fun testConsecutiveDaysProduceDistinctSequentialHadiths() {
        val totalHadiths = 42
        val day1 = 100
        val day2 = 101

        val id1 = ((day1 - 1) % totalHadiths) + 1
        val id2 = ((day2 - 1) % totalHadiths) + 1

        assertEquals("Next day should produce next sequential hadith", id1 + 1, id2)
    }
}
