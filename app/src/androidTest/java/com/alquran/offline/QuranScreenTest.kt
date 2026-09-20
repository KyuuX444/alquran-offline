package com.alquran.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alquran.offline.model.Surah
import com.alquran.offline.ui.components.SurahCard
import com.alquran.offline.ui.theme.AlQuranOfflineTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuranScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSurahCardDisplaysCorrectly() {
        val testSurah = Surah(
            id = 1,
            nameAr = "الفاتحة",
            nameLatin = "Al-Fatihah",
            translationId = "Pembukaan",
            type = "Makkiyah",
            totalVerses = 7,
            juzStart = 1,
            juzEnd = 1
        )

        composeTestRule.setContent {
            AlQuranOfflineTheme {
                SurahCard(
                    surah = testSurah,
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Al-Fatihah").assertIsDisplayed()
        composeTestRule.onNodeWithText("الفاتحة").assertIsDisplayed()
    }
}
