package com.neilturner.aerialviews.lighthouse

import android.content.Context
import com.neilturner.aerialviews.models.enums.ClockType
import com.neilturner.aerialviews.models.enums.DateType
import com.neilturner.aerialviews.models.enums.OverlayType
import com.neilturner.aerialviews.models.prefs.GeneralPrefs
import com.neilturner.aerialviews.models.videos.AerialMedia
import com.neilturner.aerialviews.services.weather.WeatherProvider
import com.neilturner.aerialviews.ui.AerialApp

class LighthouseApp : AerialApp() {
    override fun onCreate() {
        super.onCreate()
        val setup = getSharedPreferences("lighthouse_setup", MODE_PRIVATE)
        if (setup.getInt("layout_version", 0) < 2) {
            // Apply the requested layout once, including upgrades of the initial development APK.
            GeneralPrefs.fontTypeface = "jost"
            GeneralPrefs.slotTopLeft1 = OverlayType.WEATHER1
            GeneralPrefs.slotTopLeft2 = OverlayType.EMPTY
            GeneralPrefs.slotTopRight1 = OverlayType.CLOCK
            GeneralPrefs.slotTopRight2 = OverlayType.DATE
            if (GeneralPrefs.slotBottomLeft1 == OverlayType.CLOCK) GeneralPrefs.slotBottomLeft1 = OverlayType.EMPTY
            GeneralPrefs.clockFormat = ClockType.HOUR_24
            GeneralPrefs.clockSize = "28"
            GeneralPrefs.dateFormat = DateType.CUSTOM
            GeneralPrefs.dateCustom = "EEEE, d MMMM yyyy"
            GeneralPrefs.dateSize = "16"
            GeneralPrefs.weatherLocationName = "Umhlanga Rocks"
            GeneralPrefs.weatherLocationCustomName = "Umhlanga Rocks"
            GeneralPrefs.weatherLocationLat = "-29.73941"
            GeneralPrefs.weatherLocationLon = "31.0765"
            GeneralPrefs.weatherLine1Layout = "CITY,TEMPERATURE,ICON,SUMMARY"
            GeneralPrefs.weatherLine1Size = "18"
            GeneralPrefs.alternateTextPosition = false
            GeneralPrefs.showTopGradient = true
            setup.edit().putInt("layout_version", 2).apply()
        }
    }

    override fun createWeatherProvider(context: Context): WeatherProvider = OpenMeteoWeatherService()

    override fun videoLocationCaption(media: AerialMedia): String = LighthouseCaptions.format(media)
}
