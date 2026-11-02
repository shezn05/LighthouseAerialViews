package com.neilturner.aerialviews.lighthouse

import com.neilturner.aerialviews.R
import com.neilturner.aerialviews.services.weather.ForecastDay
import com.neilturner.aerialviews.services.weather.ForecastEvent
import com.neilturner.aerialviews.services.weather.WeatherEvent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

object MeteoMapper {
    fun current(
        response: MeteoResponse,
        city: String,
        fahrenheit: Boolean,
        today: LocalDate = LocalDate.now(ZoneId.of(response.timezone)),
        now: Instant = Instant.now(),
    ): WeatherEvent? {
        val current = response.current ?: return null
        val temperature = current.temperature?.takeIf { it.isFinite() } ?: return null
        val index = response.daily.time.indexOf(today.toString())
        val sunrise = clock(response.daily.sunrise.getOrNull(index))
        val sunset = clock(response.daily.sunset.getOrNull(index))
        return WeatherEvent(
            city = city,
            temperature = "${temperature.roundToInt()}°${if (fahrenheit) "F" else "C"}",
            summary = description(current.code),
            icon = icon(current.code, current.isDay == 1),
            wind = current.wind?.let { "${it.roundToInt()} km/h" } ?: "",
            humidity = current.humidity?.let { "$it%" } ?: "",
            details = "Sunrise $sunrise   ·   Sunset $sunset\n${RainOutlook.summary(response, now)}",
        )
    }

    fun forecast(
        response: MeteoResponse,
        city: String,
        days: Int,
        today: LocalDate = LocalDate.now(ZoneId.of(response.timezone)),
    ): ForecastEvent =
        ForecastEvent(
            city = city,
            days =
                response.daily.time
                    .mapIndexedNotNull { index, value ->
                        val date = runCatching { LocalDate.parse(value) }.getOrNull() ?: return@mapIndexedNotNull null
                        if (date < today) return@mapIndexedNotNull null
                        val high =
                            response.daily.highs
                                .getOrNull(index)
                                ?.takeIf { it.isFinite() } ?: return@mapIndexedNotNull null
                        val low =
                            response.daily.lows
                                .getOrNull(index)
                                ?.takeIf { it.isFinite() } ?: return@mapIndexedNotNull null
                        ForecastDay(
                            dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            icon = icon(response.daily.codes.getOrNull(index), true),
                            tempHigh = "${high.roundToInt()}°",
                            tempLow = "${low.roundToInt()}°",
                        )
                    }.take(days.coerceIn(1, 7)),
        )

    private fun clock(value: String?): String =
        value?.let {
            runCatching { LocalDateTime.parse(it).format(DateTimeFormatter.ofPattern("HH:mm")) }.getOrNull()
        } ?: "—"

    fun description(code: Int?): String =
        when (code) {
            0 -> "Clear"
            1 -> "Mostly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45, 48 -> "Fog"
            51 -> "Light drizzle"
            53, 55 -> "Drizzle"
            56, 57 -> "Freezing drizzle"
            61 -> "Light rain"
            63 -> "Rain"
            65 -> "Heavy rain"
            66, 67 -> "Freezing rain"
            71, 73, 75, 77 -> "Snow"
            80, 81, 82 -> "Rain showers"
            85, 86 -> "Snow showers"
            95, 96, 99 -> "Thunderstorms"
            else -> "Conditions unavailable"
        }

    fun icon(
        code: Int?,
        isDay: Boolean,
    ): Int =
        when (code) {
            0, 1 -> if (isDay) R.drawable.weather_clear else R.drawable.weather_clear_night
            2 -> R.drawable.weather_cloudy_light
            3 -> R.drawable.weather_cloudy_heavy
            45, 48 -> R.drawable.weather_mist
            51, 53, 55, 56, 57, 61, 66, 80 -> R.drawable.weather_rain_light
            63, 65, 67, 81, 82 -> R.drawable.weather_rain_heavy
            71, 73, 75, 77, 85, 86 -> R.drawable.weather_snow
            95, 96, 99 -> R.drawable.weather_thunder
            else -> R.drawable.weather_cloudy_light
        }
}
