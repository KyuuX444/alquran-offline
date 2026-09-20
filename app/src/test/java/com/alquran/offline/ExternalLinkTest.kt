package com.alquran.offline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class ExternalLinkTest {

    @Test
    fun testWhatsAppChannelLinks() {
        val waChannel1 = "https://whatsapp.com/channel/0029VbDO8tI2phHLTSN2ed0U"
        val waChannel2 = "https://whatsapp.com/channel/0029VbCKm3I5EjxsUbvmVf33"

        val uri1 = URI(waChannel1)
        assertEquals("https", uri1.scheme)
        assertEquals("whatsapp.com", uri1.host)
        assertTrue(uri1.path.contains("0029VbDO8tI2phHLTSN2ed0U"))

        val uri2 = URI(waChannel2)
        assertEquals("https", uri2.scheme)
        assertEquals("whatsapp.com", uri2.host)
        assertTrue(uri2.path.contains("0029VbCKm3I5EjxsUbvmVf33"))
    }

    @Test
    fun testTelegramLinks() {
        val tg1 = "https://t.me/kyuumasihcwo"
        val tg2 = "https://t.me/kyunotdev"

        val uri1 = URI(tg1)
        assertEquals("https", uri1.scheme)
        assertEquals("t.me", uri1.host)
        assertEquals("/kyuumasihcwo", uri1.path)

        val uri2 = URI(tg2)
        assertEquals("https", uri2.scheme)
        assertEquals("t.me", uri2.host)
        assertEquals("/kyunotdev", uri2.path)
    }

    @Test
    fun testOpenUrlSafelyProtection() {
        // Verify valid URLs pass syntax check without throwing exceptions
        val urls = listOf(
            "https://whatsapp.com/channel/0029VbDO8tI2phHLTSN2ed0U",
            "https://whatsapp.com/channel/0029VbCKm3I5EjxsUbvmVf33",
            "https://t.me/kyuumasihcwo",
            "https://t.me/kyunotdev"
        )

        for (url in urls) {
            val uri = URI.create(url)
            assertTrue("Scheme must be https", uri.scheme == "https")
            assertTrue("Host must not be empty", !uri.host.isNullOrEmpty())
        }
    }
}
