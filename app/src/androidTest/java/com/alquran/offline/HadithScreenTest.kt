package com.alquran.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alquran.offline.model.Hadith
import com.alquran.offline.ui.screens.hadith.HadithItemCard
import com.alquran.offline.ui.theme.AlQuranOfflineTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HadithScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHadithCardDisplaysCorrectly() {
        val testHadith = Hadith(
            id = 1,
            kitab = "Arba'in An-Nawawi",
            nomor = 1,
            judul = "Niat dan Ikhlas",
            sumber = "HR. Bukhari dan Muslim",
            teksAr = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            teksId = "Sesungguhnya setiap amalan tergantung pada niatnya.",
            tema = "Keikhlasan",
            isBookmarked = false
        )

        composeTestRule.setContent {
            AlQuranOfflineTheme {
                HadithItemCard(
                    hadith = testHadith,
                    onClick = {},
                    onBookmarkClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Niat dan Ikhlas").assertIsDisplayed()
        composeTestRule.onNodeWithText("HR. Bukhari dan Muslim").assertIsDisplayed()
    }
}
