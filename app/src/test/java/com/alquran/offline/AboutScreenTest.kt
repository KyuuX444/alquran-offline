package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AboutScreenTest {

    @Test
    fun testDeveloperAndAppMetadata() {
        val devName = "Kyuryn (S.D.Y)"
        val appName = "Al-Qur'an Offline"
        val version = "1.0"

        assertEquals("Kyuryn (S.D.Y)", devName)
        assertEquals("Al-Qur'an Offline", appName)
        assertEquals("1.0", version)
    }

    @Test
    fun testAboutScreenSourceContainsRequiredSections() {
        val candidates = listOf(
            File("src/main/java/com/alquran/offline/ui/screens/about/AboutScreen.kt"),
            File("app/src/main/java/com/alquran/offline/ui/screens/about/AboutScreen.kt"),
            File("/root/Alquran/app/src/main/java/com/alquran/offline/ui/screens/about/AboutScreen.kt")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertTrue("AboutScreen.kt must exist", file != null && file.exists())

        val source = file!!.readText(Charsets.UTF_8)

        // Verify required sections are present
        assertTrue("Must contain App Name", source.contains("Al-Qur'an Offline"))
        assertTrue("Must contain Version 1.0", source.contains("Version 1.0"))
        assertTrue("Must contain Tentang Aplikasi", source.contains("Tentang Aplikasi"))
        assertTrue("Must contain Fitur Aplikasi", source.contains("Fitur Aplikasi"))
        assertTrue("Must contain Dikembangkan oleh", source.contains("Dikembangkan oleh"))
        assertTrue("Must contain Developer Name Kyuryn (S.D.Y)", source.contains("Kyuryn (S.D.Y)"))
        assertTrue("Must contain Tech Stack", source.contains("Tech Stack"))
        assertTrue("Must contain Terhubung", source.contains("Terhubung"))
        assertTrue("Must contain Sumber & Kredit", source.contains("Sumber & Kredit"))
        assertTrue("Must contain Lisensi", source.contains("Lisensi"))
        assertTrue("Must contain Footer attribution", source.contains("Made with care by Kyuryn (S.D.Y)"))
    }
}
