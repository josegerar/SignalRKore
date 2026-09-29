package eu.lepicekmichal.signalrkore

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class HandshakePayloadTest {

    @Test
    fun separatesHandshakeAndHubMessageWhenTheyShareATransportFrame() {
        val invocation = """{"type":1,"target":"ServerStatus","arguments":[true]}"""
        val payload = "{}$RECORD_SEPARATOR$invocation$RECORD_SEPARATOR".encodeToByteArray()

        val (handshake, remainder) = splitHandshakePayload(payload)

        assertEquals("{}", handshake)
        assertEquals("$invocation$RECORD_SEPARATOR", remainder?.decodeToString())
    }

    @Test
    fun acceptsHandshakeWithNoFollowingHubMessage() {
        val payload = "{}$RECORD_SEPARATOR".encodeToByteArray()

        val (handshake, remainder) = splitHandshakePayload(payload)

        assertEquals("{}", handshake)
        assertNull(remainder)
    }

    @Test
    fun rejectsPayloadWithoutHandshakeTerminator() {
        assertFailsWith<RuntimeException> {
            splitHandshakePayload("{}".encodeToByteArray())
        }
    }
}
