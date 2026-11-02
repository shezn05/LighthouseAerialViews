package com.neilturner.aerialviews.lighthouse

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime

class RainOutlookTest {
    private val now = Instant.parse("2026-11-02T11:20:00Z") // 13:20 in Umhlanga

    private fun response(
        probabilities: List<Int?>,
        firstEnd: String = "2026-11-02T14:00",
    ): MeteoResponse =
        MeteoResponse(
            utcOffsetSeconds = 7200,
            hourly =
                MeteoHourly(
                    time = probabilities.indices.map { LocalDateTime.parse(firstEnd).plusHours(it.toLong()).toString() },
                    probability = probabilities,
                ),
        )

    @Test
    fun `peak chance and first contiguous wet window use the preceding hour`() {
        assertEquals(
            "Rain next 6h: up to 80%\nLikely rain 14:00–16:00",
            RainOutlook.summary(response(listOf(20, 60, 80, 10, 70, 20)), now),
        )
    }

    @Test
    fun `low probability does not promise dry weather`() {
        assertEquals("Rain next 6h: up to 20%", RainOutlook.summary(response(listOf(0, 10, 20, 10, 0, 5)), now))
    }

    @Test
    fun `partial invalid and missing data are unavailable rather than zero chance`() {
        listOf(emptyList(), listOf(0, 0), listOf(0, 0, null, 0, 0, 0), listOf(0, 0, 101, 0, 0, 0)).forEach {
            assertEquals("Rain outlook unavailable", RainOutlook.summary(response(it), now))
        }
        val source = response(List(6) { 0 })
        val malformed = source.copy(hourly = source.hourly.copy(time = listOf("bad") + source.hourly.time.drop(1)))
        assertEquals("Rain outlook unavailable", RainOutlook.summary(malformed, now))
    }

    @Test
    fun `past rows and stale forecasts cannot produce a fresh low chance`() {
        assertEquals("Rain outlook unavailable", RainOutlook.summary(response(List(6) { 0 }, "2026-11-02T06:00"), now))
        val extended = response(listOf(100, 100, 0, 10, 20, 0, 0, 0), "2026-11-02T12:00")
        assertEquals("Rain next 6h: up to 20%", RainOutlook.summary(extended, now))
    }

    @Test
    fun `crossing midnight labels tomorrow and respects API offset`() {
        val source = response(listOf(0, 0, 60, 80, 0, 0), "2026-11-02T23:00")
        assertEquals(
            "Rain next 6h: up to 80%\nLikely rain Tomorrow 00:00–Tomorrow 02:00",
            RainOutlook.summary(source, Instant.parse("2026-11-02T20:20:00Z")),
        )
        val offsetSource = source.copy(timezone = "UTC")
        assertEquals(
            RainOutlook.summary(source, Instant.parse("2026-11-02T20:20:00Z")),
            RainOutlook.summary(offsetSource, Instant.parse("2026-11-02T20:20:00Z")),
        )
    }

    @Test
    fun `hour boundary selects the next interval and preserves a real zero`() {
        assertEquals("Rain next 6h: up to 0%", RainOutlook.summary(response(List(6) { 0 }), Instant.parse("2026-11-02T11:00:00Z")))
    }

    @Test
    fun `hourly fields deserialize including null probabilities`() {
        val source =
            Json.decodeFromString<MeteoResponse>(
                """{"utc_offset_seconds":7200,"hourly":{"time":["2026-11-02T14:00"],"precipitation_probability":[null]}}""",
            )
        assertEquals(7200, source.utcOffsetSeconds)
        assertEquals(listOf<Int?>(null), source.hourly.probability)
    }
}
