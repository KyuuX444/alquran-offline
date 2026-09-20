package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.net.URI

class ShortcutTest {

    @Test
    fun testShortcutsXmlExistsAndContainsAllRequiredShortcuts() {
        val pathCandidates = listOf(
            File("src/main/res/xml/shortcuts.xml"),
            File("app/src/main/res/xml/shortcuts.xml"),
            File("/root/Alquran/app/src/main/res/xml/shortcuts.xml")
        )
        val file = pathCandidates.firstOrNull { it.exists() }
        assertNotNull("shortcuts.xml must exist", file)

        val content = file!!.readText()
        assertTrue("Must have shortcut_last_read", content.contains("shortcut_last_read"))
        assertTrue("Must have shortcut_surah", content.contains("shortcut_surah"))
        assertTrue("Must have shortcut_hadith", content.contains("shortcut_hadith"))
        assertTrue("Must have shortcut_juz", content.contains("shortcut_juz"))

        assertTrue("Must target alquran://last_read", content.contains("alquran://last_read"))
        assertTrue("Must target alquran://surah_list", content.contains("alquran://surah_list"))
        assertTrue("Must target alquran://hadith", content.contains("alquran://hadith"))
        assertTrue("Must target alquran://juz_list", content.contains("alquran://juz_list"))
    }

    @Test
    fun testDeepLinkUriParsing() {
        val uri1 = URI.create("alquran://last_read")
        assertEquals("alquran", uri1.scheme)
        assertEquals("last_read", uri1.host)

        val uri2 = URI.create("alquran://surah_list")
        assertEquals("surah_list", uri2.host)

        val uri3 = URI.create("alquran://hadith?id=15")
        assertEquals("hadith", uri3.host)
        assertTrue(uri3.query.contains("id=15"))

        val uri4 = URI.create("alquran://juz_list")
        assertEquals("juz_list", uri4.host)
    }
}
