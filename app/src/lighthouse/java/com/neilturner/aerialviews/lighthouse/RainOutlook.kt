package com.neilturner.aerialviews.lighthouse

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Hourly forecast, not a minute-accurate rain countdown or a six-hour combined probability. */
object RainOutlook {
    private const val HOURS = 6
    private const val LIKELY_PERCENT = 50

    fun summary(
        response: MeteoResponse,
        now: Instant = Instant.now(),
    ): String {
        // ISO timestamps use the API's fixed offset even if DST changes during the forecast.
        val zone = response.utcOffsetSeconds?.let(ZoneOffset::ofTotalSeconds) ?: ZoneId.of(response.timezone)
        val localNow = LocalDateTime.ofInstant(now, zone)
        val firstEnd = localNow.truncatedTo(ChronoUnit.HOURS).plusHours(1)
        val probabilities =
            response.hourly.time
                .mapIndexedNotNull { index, timestamp ->
                    val end = runCatching { LocalDateTime.parse(timestamp) }.getOrNull() ?: return@mapIndexedNotNull null
                    val probability =
                        response.hourly.probability
                            .getOrNull(index)
                            ?.takeIf { it in 0..100 }
                            ?: return@mapIndexedNotNull null
                    end to probability
                }.toMap()
        // A missing hour must not become a reassuring low/zero chance.
        val hours =
            (0 until HOURS).map { index ->
                val end = firstEnd.plusHours(index.toLong())
                end to (probabilities[end] ?: return "Rain outlook unavailable")
            }
        val peak = hours.maxOf { it.second }
        val heading = "Rain next 6h: up to $peak%"
        val firstWetIndex = hours.indexOfFirst { it.second >= LIKELY_PERCENT }
        if (firstWetIndex < 0) return heading

        // Show the first contiguous likely window; do not bridge a dry gap.
        val wetWindow = hours.drop(firstWetIndex).takeWhile { it.second >= LIKELY_PERCENT }
        val start = wetWindow.first().first.minusHours(1)
        val end = wetWindow.last().first

        fun label(time: LocalDateTime): String {
            val prefix = if (time.toLocalDate() == localNow.toLocalDate()) "" else "Tomorrow "
            return prefix + time.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        return "$heading\nLikely rain ${label(start)}–${label(end)}"
    }
}
