package com.neilturner.aerialviews.lighthouse

import com.neilturner.aerialviews.data.network.JsonHelper.buildSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("timezone") timezone: String = "auto",
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,is_day,wind_speed_10m",
        @Query("daily") daily: String = "sunrise,sunset,weather_code,temperature_2m_max,temperature_2m_min",
        @Query("forecast_days") days: Int = 7,
    ): Response<MeteoResponse>
}

interface MeteoGeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
    ): MeteoLocations
}

object MeteoClients {
    private val client =
        OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()

    private fun retrofit(url: String): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(buildSerializer())
            .build()

    val weather: OpenMeteoApi by lazy { retrofit("https://api.open-meteo.com/").create(OpenMeteoApi::class.java) }
    val geocoding: MeteoGeocodingApi by lazy { retrofit("https://geocoding-api.open-meteo.com/").create(MeteoGeocodingApi::class.java) }
}

@Serializable
data class MeteoResponse(
    val timezone: String = "Africa/Johannesburg",
    val current: MeteoCurrent? = null,
    val daily: MeteoDaily = MeteoDaily(),
)

@Serializable
data class MeteoCurrent(
    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("weather_code") val code: Int? = null,
    @SerialName("is_day") val isDay: Int = 1,
    @SerialName("relative_humidity_2m") val humidity: Int? = null,
    @SerialName("wind_speed_10m") val wind: Double? = null,
)

@Serializable
data class MeteoDaily(
    val time: List<String> = emptyList(),
    val sunrise: List<String?> = emptyList(),
    val sunset: List<String?> = emptyList(),
    @SerialName("weather_code") val codes: List<Int?> = emptyList(),
    @SerialName("temperature_2m_max") val highs: List<Double?> = emptyList(),
    @SerialName("temperature_2m_min") val lows: List<Double?> = emptyList(),
)

@Serializable
data class MeteoLocations(
    val results: List<MeteoLocation> = emptyList(),
)

@Serializable
data class MeteoLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("country_code") val countryCode: String = "",
    val admin1: String? = null,
)
