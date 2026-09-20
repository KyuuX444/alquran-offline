package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AsmaulHusnaTest {

    private data class RawAsmaulHusna(
        val id: Int,
        val nameAr: String,
        val nameLatin: String,
        val meaningId: String
    )

    private fun getAsmaulHusnaFile(): File {
        val candidates = listOf(
            File("src/main/assets/asmaul_husna/asmaul_husna.json"),
            File("app/src/main/assets/asmaul_husna/asmaul_husna.json"),
            File("/root/Alquran/app/src/main/assets/asmaul_husna/asmaul_husna.json")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertNotNull("asmaul_husna.json must exist in assets", file)
        return file!!
    }

    private fun loadAsmaulHusna(): List<RawAsmaulHusna> {
        val file = getAsmaulHusnaFile()
        val text = file.readText(Charsets.UTF_8)
        val items = mutableListOf<RawAsmaulHusna>()
        val objRegex = Regex("\\{[^{}]*\"nameAr\"[^{}]*\\}", RegexOption.DOT_MATCHES_ALL)

        for (match in objRegex.findAll(text)) {
            val block = match.value
            fun extractString(key: String): String {
                val r = Regex("\"$key\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                val raw = r.find(block)?.groupValues?.get(1) ?: ""
                return raw.replace("\\\"", "\"").replace("\\n", "\n")
            }
            fun extractInt(key: String): Int {
                val r = Regex("\"$key\"\\s*:\\s*(\\d+)")
                return r.find(block)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }

            val id = extractInt("id")
            val nameAr = extractString("nameAr")
            val nameLatin = extractString("nameLatin")
            val meaningId = extractString("meaningId")

            if (id > 0) {
                items.add(
                    RawAsmaulHusna(
                        id = id,
                        nameAr = nameAr,
                        nameLatin = nameLatin,
                        meaningId = meaningId
                    )
                )
            }
        }
        return items
    }

    @Test
    fun testAsmaulHusnaCountIs99() {
        val list = loadAsmaulHusna()
        assertEquals("Total Asmaul Husna must be exactly 99", 99, list.size)
    }

    @Test
    fun testFirstAndLastAsmaulHusna() {
        val list = loadAsmaulHusna()
        val first = list.first()
        val last = list.last()

        assertEquals(1, first.id)
        assertTrue(first.nameLatin.contains("Rahman"))
        assertTrue(first.meaningId.contains("Pengasih"))

        assertEquals(99, last.id)
        assertTrue(last.nameLatin.contains("Shabuur") || last.nameLatin.contains("Sabur"))
        assertTrue(last.meaningId.contains("Sabar"))
    }

    @Test
    fun testAllFieldsNonEmptyAndSequential() {
        val list = loadAsmaulHusna()
        for (i in 0 until 99) {
            val item = list[i]
            assertEquals("ID must be sequential 1..99", i + 1, item.id)
            assertTrue("Arabic name must not be blank", item.nameAr.isNotBlank())
            assertTrue("Latin name must not be blank", item.nameLatin.isNotBlank())
            assertTrue("Meaning must not be blank", item.meaningId.isNotBlank())
        }
    }

    @Test
    fun testSearchFiltering() {
        val list = loadAsmaulHusna()
        val searchResult = list.filter {
            it.nameLatin.lowercase().contains("rahman") || it.meaningId.lowercase().contains("pengasih")
        }
        assertTrue("Search for 'rahman' should find at least 1 result", searchResult.isNotEmpty())
        assertEquals(1, searchResult.first().id)
    }
}
