package com.neilturner.aerialviews.lighthouse

import com.neilturner.aerialviews.R
import com.neilturner.aerialviews.models.prefs.GeneralPrefs
import com.neilturner.aerialviews.services.weather.LocationResponse
import com.neilturner.aerialviews.services.weather.TemperatureUnit
import com.neilturner.aerialviews.services.weather.WeatherEvent
import com.neilturner.aerialviews.services.weather.WeatherProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.kosert.flowbus.GlobalBus
import timber.log.Timber
import kotlin.time.Duration.Companion.minutes

/** Open-Meteo personal-use provider. Requests stop when the screensaver stops. */
class OpenMeteoWeatherService(
    private val api: OpenMeteoApi = MeteoClients.weather,
    private val geocoding: MeteoGeocodingApi = MeteoClients.geocoding,
) : WeatherProvider {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var updateJob: Job? = null

    override suspend fun lookupLocation(query: String): List<LocationResponse> =
        geocoding.search(query).results.map {
            LocationResponse(it.name, it.latitude, it.longitude, it.countryCode, it.admin1)
        }

    override suspend fun lookupLocationByCoordinates(
        lat: Double,
        lon: Double,
    ): List<LocationResponse> {
        require(lat in -90.0..90.0 && lon in -180.0..180.0)
        // Open-Meteo has no reverse geocoder. Keep explicit coordinates usable without inventing a city.
        return listOf(LocationResponse("$lat, $lon", lat, lon, "GPS"))
    }

    override fun startUpdates(
        fetchCurrentWeather: Boolean,
        fetchForecast: Boolean,
    ) {
        updateJob?.cancel()
        if (!fetchCurrentWeather && !fetchForecast) return
        updateJob =
            scope.launch {
                while (isActive) {
                    var nextUpdate = 20.minutes
                    val city =
                        GeneralPrefs.weatherLocationCustomName.ifBlank {
                            GeneralPrefs.weatherLocationName.ifBlank { "Umhlanga Rocks" }
                        }
                    try {
                        val lat = GeneralPrefs.weatherLocationLat.toDoubleOrNull()
                        val lon = GeneralPrefs.weatherLocationLon.toDoubleOrNull()
                        require(lat != null && lon != null && lat in -90.0..90.0 && lon in -180.0..180.0)
                        val fahrenheit = GeneralPrefs.weatherTemperatureUnits == TemperatureUnit.IMPERIAL
                        val result = api.forecast(lat, lon, if (fahrenheit) "fahrenheit" else "celsius")
                        check(result.isSuccessful) { "Open-Meteo HTTP ${result.code()}" }
                        val body = checkNotNull(result.body()) { "Empty weather response" }
                        if (fetchCurrentWeather) {
                            val weather = checkNotNull(MeteoMapper.current(body, city, fahrenheit)) { "Current weather unavailable" }
                            GlobalBus.post(weather)
                        }
                        if (fetchForecast) {
                            GlobalBus.post(MeteoMapper.forecast(body, city, GeneralPrefs.weatherLine2Days.toIntOrNull() ?: 3))
                        }
                        Timber.i("Lighthouse weather updated from Open-Meteo")
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Exception) {
                        Timber.w(error, "Lighthouse weather unavailable; retrying in 5 minutes")
                        nextUpdate = 5.minutes
                        if (fetchCurrentWeather) {
                            GlobalBus.post(
                                WeatherEvent(
                                    city = city,
                                    temperature = "—",
                                    icon = R.drawable.weather_cloudy_light,
                                    summary = "Weather unavailable",
                                    details = "Waiting for an update",
                                ),
                            )
                        }
                    }
                    delay(nextUpdate)
                }
            }
    }

    override fun stop() {
        scope.cancel()
        updateJob = null
    }
}
