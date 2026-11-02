package com.neilturner.aerialviews.lighthouse

import com.neilturner.aerialviews.R
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MeteoMapperTest {
    private val today = LocalDate.of(2026, 11, 2)
    private val response =
        MeteoResponse(
            current = MeteoCurrent(23.6, 51, 1, 74, 12.7),
            daily =
                MeteoDaily(
                    time = listOf("2026-11-01", "2026-11-02", "2026-11-03"),
                    sunrise = listOf("2026-11-01T05:01", "2026-11-02T05:00", "2026-11-03T04:59"),
                    sunset = listOf("2026-11-01T18:17", "2026-11-02T18:18", "2026-11-03T18:19"),
                    codes = listOf(0, 51, 80),
                    highs = listOf(23.0, 24.2, 25.6),
                    lows = listOf(20.0, 20.4, 21.8),
                ),
        )

    @Test
    fun `uses requested city and today's local sunrise and sunset`() {
        val event = MeteoMapper.current(response, "Umhlanga Rocks", false, today)!!
        assertEquals("Umhlanga Rocks", event.city)
        assertEquals("24°C", event.temperature)
        assertEquals("Light drizzle", event.summary)
        assertEquals("Sunrise 05:00   ·   Sunset 18:18", event.details)
    }

    @Test
    fun `does not reuse previous day's solar times after midnight`() {
        val event = MeteoMapper.current(response, "Umhlanga Rocks", false, today.plusDays(1))!!
        assertEquals("Sunrise 04:59   ·   Sunset 18:19", event.details)
    }

    @Test
    fun `missing solar times show dashes instead of invented values`() {
        val event = MeteoMapper.current(response.copy(daily = MeteoDaily()), "Umhlanga Rocks", false, today)!!
        assertEquals("Sunrise —   ·   Sunset —", event.details)
    }

    @Test
    fun `invalid solar time does not discard current weather`() {
        val daily = response.daily.copy(sunrise = listOf(null, "not a time"))
        val event = MeteoMapper.current(response.copy(daily = daily), "Umhlanga Rocks", false, today)!!
        assertEquals("24°C", event.temperature)
        assertEquals("Sunrise —   ·   Sunset 18:18", event.details)
    }

    @Test
    fun `missing temperature is not displayed as zero`() {
        assertNull(MeteoMapper.current(response.copy(current = MeteoCurrent()), "Umhlanga Rocks", false, today))
        assertNull(MeteoMapper.current(response.copy(current = null), "Umhlanga Rocks", false, today))
    }

    @Test
    fun `non finite temperature is rejected`() {
        val current = MeteoCurrent(temperature = Double.NaN)
        assertNull(MeteoMapper.current(response.copy(current = current), "Umhlanga Rocks", false, today))
    }

    @Test
    fun `fahrenheit response keeps its unit`() {
        val current = MeteoCurrent(temperature = 74.6)
        assertEquals("75°F", MeteoMapper.current(response.copy(current = current), "Umhlanga Rocks", true, today)!!.temperature)
    }

    @Test
    fun `forecast excludes yesterday and honours requested day count`() {
        val forecast = MeteoMapper.forecast(response, "Umhlanga Rocks", 1, today)
        assertEquals(1, forecast.days.size)
        assertEquals("24°", forecast.days.single().tempHigh)
        assertEquals("20°", forecast.days.single().tempLow)
    }

    @Test
    fun `incomplete forecast rows are skipped safely`() {
        val daily = response.daily.copy(highs = listOf(23.0, null, 25.6))
        val forecast = MeteoMapper.forecast(response.copy(daily = daily), "Umhlanga Rocks", 3, today)
        assertEquals(1, forecast.days.size)
        assertEquals("26°", forecast.days.single().tempHigh)
    }

    @Test
    fun `unknown conditions are not labelled clear`() {
        assertEquals("Conditions unavailable", MeteoMapper.description(null))
        assertEquals("Conditions unavailable", MeteoMapper.description(999))
    }

    @Test
    fun `night clear sky uses the night icon`() {
        assertEquals(R.drawable.weather_clear_night, MeteoMapper.icon(0, false))
        assertEquals(R.drawable.weather_clear, MeteoMapper.icon(0, true))
        assertEquals(R.drawable.weather_thunder, MeteoMapper.icon(95, true))
    }

    @Test
    fun `parses API field names and ignores extra metadata`() {
        val parsed =
            Json { ignoreUnknownKeys = true }.decodeFromString<MeteoResponse>(
                """{"timezone":"Africa/Johannesburg","generationtime_ms":0.5,"current":{"temperature_2m":23.6,"weather_code":51,"is_day":1,"time":"2026-11-02T13:00"},"daily":{"time":["2026-11-02"],"sunrise":["2026-11-02T05:00"],"sunset":["2026-11-02T18:18"]}}""",
            )
        assertEquals("24°C", MeteoMapper.current(parsed, "Umhlanga Rocks", false, today)!!.temperature)
    }
}
