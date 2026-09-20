package com.alquran.offline

import com.alquran.offline.provenance.BuildOrigin
import com.alquran.offline.provenance.ProjectInfo
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ProvenanceTest {

    private fun findFile(relativePaths: List<String>): File {
        for (rel in relativePaths) {
            val f = File(rel)
            if (f.exists()) return f
            val inApp = File("app", rel)
            if (inApp.exists()) return inApp
        }
        throw AssertionError("File not found in paths: $relativePaths")
    }

    @Test
    fun testProjectInfoConstants() {
        assertEquals("Al-Qur'an Offline", ProjectInfo.NAME)
        assertEquals("Kyuu", ProjectInfo.AUTHOR)
        assertEquals("KyuuX444", ProjectInfo.GITHUB)
        assertEquals("quran-offline-kyuu-2026-c9f2a87b", ProjectInfo.PROJECT_ID)
        assertEquals("© 2026 Kyuu / KyuuX444", ProjectInfo.COPYRIGHT)
        assertEquals("com.alquran.offline", ProjectInfo.OFFICIAL_PACKAGE_NAME)
        assertEquals("MIT License", ProjectInfo.LICENSE)
        assertEquals("1.0", ProjectInfo.VERSION_NAME)
        assertEquals(1, ProjectInfo.VERSION_CODE)
        assertTrue(ProjectInfo.OFFLINE_VERIFIED)
    }

    @Test
    fun testAssetProvenanceJsonMatchesProjectInfo() {
        val file = findFile(listOf(
            "src/main/assets/provenance.json",
            "app/src/main/assets/provenance.json"
        ))
        assertTrue("provenance.json must exist in assets", file.exists())
        val json = JSONObject(file.readText(Charsets.UTF_8))

        assertEquals(ProjectInfo.NAME, json.getString("project_name"))
        assertEquals(ProjectInfo.PROJECT_ID, json.getString("project_id"))
        assertEquals(ProjectInfo.AUTHOR, json.getString("author"))
        assertEquals(ProjectInfo.GITHUB, json.getString("github"))
        assertEquals(ProjectInfo.COPYRIGHT, json.getString("copyright"))
        assertEquals(ProjectInfo.VERSION_NAME, json.getString("version_name"))
        assertEquals(ProjectInfo.VERSION_CODE, json.getInt("version_code"))
        assertTrue(json.getBoolean("verified_offline"))
        assertFalse(json.getBoolean("telemetry_enabled"))
        assertFalse(json.getBoolean("internet_required"))
    }

    @Test
    fun testResourceProvenanceXmlContainsProjectIdentifiers() {
        val file = findFile(listOf(
            "src/main/res/values/provenance.xml",
            "app/src/main/res/values/provenance.xml"
        ))
        assertTrue("provenance.xml must exist in res/values", file.exists())
        val content = file.readText(Charsets.UTF_8)

        assertTrue(content.contains(ProjectInfo.PROJECT_ID))
        assertTrue(content.contains(ProjectInfo.AUTHOR))
        assertTrue(content.contains(ProjectInfo.GITHUB))
        assertTrue(content.contains(ProjectInfo.COPYRIGHT))
    }

    @Test
    fun testBuildOriginEnumHasNonDestructiveDescriptions() {
        for (origin in BuildOrigin.values()) {
            assertNotNull(origin.label)
            assertTrue(origin.label.isNotBlank())
            assertNotNull(origin.description)
            assertTrue(origin.description.isNotBlank())
            // Verify descriptions are informative and polite, not aggressive or threatening
            assertFalse(origin.description.contains("banned"))
            assertFalse(origin.description.contains("destroy"))
            assertFalse(origin.description.contains("terminated"))
        }
    }
}
