package com.neilturner.aerialviews.lighthouse

import android.net.Uri
import com.neilturner.aerialviews.models.enums.AerialMediaSource
import com.neilturner.aerialviews.models.enums.SceneType
import com.neilturner.aerialviews.models.videos.AerialExifMetadata
import com.neilturner.aerialviews.models.videos.AerialMedia
import com.neilturner.aerialviews.models.videos.AerialMediaMetadata
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LighthouseCaptionsTest {
    private fun media(
        description: String = "",
        filename: String = "test.mov",
    ): AerialMedia {
        val uri = mockk<Uri>()
        every { uri.lastPathSegment } returns filename
        return AerialMedia(uri, source = AerialMediaSource.APPLE, metadata = AerialMediaMetadata(shortDescription = description))
    }

    @Test
    fun `structured city or state and country take priority over catalog labels`() {
        val source = media("London")
        source.metadata.exif = AerialExifMetadata(city = "Umhlanga Rocks", state = "KwaZulu-Natal", country = "South Africa")
        assertEquals("Umhlanga Rocks, South Africa", LighthouseCaptions.format(source))
        source.metadata.exif.city = null
        assertEquals("KwaZulu-Natal, South Africa", LighthouseCaptions.format(source))
    }

    @Test
    fun `city and broad natural region captions omit the scene narrative`() {
        assertEquals("London, United Kingdom", LighthouseCaptions.format(media("London")))
        assertEquals("Alaska, United States", LighthouseCaptions.format(media("Alaskan Jellies")))
        assertEquals("Palau", LighthouseCaptions.format(media("Palau Jellies")))
    }

    @Test
    fun `stable community identity supports quality variants and case differences`() {
        assertEquals("Miami, United States", LighthouseCaptions.format(media("Florida", "VIDEO_INSPIRE_FLORIDA_MIAMI_brickell_4k.mov")))
        assertEquals("Réunion, France", LighthouseCaptions.format(media("France", "robinfrcd_reunion_CapMechant_1080p_h264.m4v")))
    }

    @Test
    fun `unknown places and orbital routes do not invent a country`() {
        assertEquals("", LighthouseCaptions.format(media("Uncatalogued reef")))
        val source = media("China")
        source.metadata.scene = SceneType.SPACE
        assertEquals("", LighthouseCaptions.format(source))
        val custom = media("Seals").copy(source = AerialMediaSource.CUSTOM)
        assertEquals("", LighthouseCaptions.format(custom))
    }
}
