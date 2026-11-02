package com.neilturner.aerialviews.services.weather

/** Allows a branded application to supply weather without changing screensaver playback. */
interface WeatherProvider {
    suspend fun lookupLocation(query: String): List<LocationResponse>

    suspend fun lookupLocationByCoordinates(
        lat: Double,
        lon: Double,
    ): List<LocationResponse>

    fun startUpdates(
        fetchCurrentWeather: Boolean,
        fetchForecast: Boolean,
    )

    fun stop()
}
