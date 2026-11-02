package com.neilturner.aerialviews.lighthouse

import com.neilturner.aerialviews.models.enums.AerialMediaSource
import com.neilturner.aerialviews.models.enums.SceneType
import com.neilturner.aerialviews.models.videos.AerialMedia
import com.neilturner.aerialviews.utils.filenameWithoutExtension

/** Display-only labels sourced from the bundled catalogs. Original metadata remains intact. */
object LighthouseCaptions {
    fun format(media: AerialMedia): String {
        val exif = media.metadata.exif
        val country = exif.country?.trim()?.takeIf { it.isNotEmpty() }
        val place = listOf(exif.city, exif.state).firstOrNull { !it.isNullOrBlank() }?.trim()
        if (country != null) return listOfNotNull(place?.takeUnless { it.equals(country, true) }, country).joinToString(", ")
        // Orbital videos cross countries; do not label the entire flight as one city.
        if (media.metadata.scene == SceneType.SPACE) return ""
        val filename = media.uri.filenameWithoutExtension
        community.entries.firstOrNull { filename.startsWith(it.key, ignoreCase = true) }?.let { return it.value }
        if (media.source !in setOf(AerialMediaSource.APPLE, AerialMediaSource.AMAZON)) return ""
        return locations[media.metadata.shortDescription.trim()].orEmpty()
    }

    // Use a city when known, otherwise the region/state. Unknown places stay blank.
    // Country-only labels are intentional for footage without a more precise location.
    private val locations =
        mapOf(
            "Liwa" to "Liwa, United Arab Emirates",
            "Scotland" to "Scotland, United Kingdom",
            "Hawaii" to "Hawaii, United States",
            "Patagonia" to "Patagonia, Chile",
            "Iceland" to "Iceland",
            "Greenland" to "Greenland",
            "Yosemite" to "California, United States",
            "Grand Canyon" to "Arizona, United States",
            "Dubai" to "Dubai, United Arab Emirates",
            "Hong Kong" to "Hong Kong, China",
            "San Francisco" to "San Francisco, United States",
            "Los Angeles" to "Los Angeles, United States",
            "New York" to "New York, United States",
            "London" to "London, United Kingdom",
            "China" to "China",
            "Tahiti Waves" to "Tahiti, French Polynesia",
            "California Dolphins" to "California, United States",
            "Gray Reef Sharks" to "French Polynesia",
            "Cownose Rays" to "Mexico",
            "Kelp" to "Cape Peninsula, South Africa",
            "Red Sea Coral" to "Red Sea, Egypt",
            "Seals" to "Cape Peninsula, South Africa",
            "Palau Jellies" to "Palau",
            "Alaskan Jellies" to "Alaska, United States",
            "Costa Rica Dolphins" to "Costa Rica",
            "Humpback Whale" to "French Polynesia",
            "California Kelp Forest" to "California, United States",
            "Palau Coral" to "Palau",
            "California Wildflowers" to "California, United States",
            "California State Route 58, Carrizo Plain, California" to "California, United States",
            "Del Norte Coast Redwoods State Park, California" to "California, United States",
            "Redwoods" to "California, United States",
            "China Beach, Curry County, Oregon" to "Oregon, United States",
            "Secret Beach, Brookings, Oregon" to "Brookings, United States",
            "Monument Valley, Utah" to "Utah, United States",
            "Coal Mine Canyon, Arizona" to "Arizona, United States",
            "Cathedral Canyon, Lake Powell, Utah" to "Utah, United States",
            "Factory Butte, Wayne County, Utah" to "Utah, United States",
            "Twilight, Forbidding, and Cascade Canyon, Lake Powell, Utah" to "Utah, United States",
            "Olympia Bar, Lake Powell, Utah" to "Utah, United States",
            "Ghansali from Above" to "Uttarakhand, India",
            "Through Himalayas" to "Uttarakhand, India",
            "Tea Gardens from Above" to "Munnar, India",
            "Goa Beaches" to "Goa, India",
            "Reservoir Day" to "Tamil Nadu, India",
            "Goa Coast" to "Goa, India",
            "Himalayas Day" to "Uttarakhand, India",
            "Tea Gardens Day" to "Munnar, India",
            "The Ganges" to "Rishikesh, India",
            "Himalayan Peaks" to "Uttarakhand, India",
            "Tea Gardens Mist" to "Munnar, India",
            "Fish Tower" to "Cabo Pulmo, Mexico",
            "Sardine Hunters" to "Magdalena Bay, Mexico",
            "Magdalena Bay Sea Cliffs" to "Magdalena Bay, Mexico",
            "Magdalena Bay Sunset" to "Magdalena Bay, Mexico",
            "The Arch of Cabo San Lucas" to "Cabo San Lucas, Mexico",
            "Beach from Above" to "Cabo San Lucas, Mexico",
            "Cabo Pulmo Beaches" to "Cabo Pulmo, Mexico",
            "Chasing Mackerel" to "Magdalena Bay, Mexico",
            "Soda lake bloom" to "California, United States",
            "Morning mist" to "California, United States",
            "Carizzo wildflowers" to "California, United States",
            "Trona sunrise and beachside blooms" to "California, United States",
            "Northern Fjords of Norway" to "Norway",
            "Lofoten Islands" to "Lofoten, Norway",
            "Baboons" to "Namibia",
            "Flamingo Bay" to "Namibia",
            "Dunes Of Sossuvlei" to "Sossusvlei, Namibia",
            "Namibia Landscapes" to "Namibia",
            "Spitzkoppe Peak" to "Namibia",
            "Coastal Sand Dunes" to "Namibia",
            "Cloud Covered Mountain" to "Dolomites, Italy",
            "Snow Flurry by the River" to "California, United States",
            "Peaceful Snowy Afternoon" to "Aomori, Japan",
            "Mountain River" to "Bavaria, Germany",
            "Lavender Fields Motion" to "Provence, France",
            "Turquoise Coast" to "Ölüdeniz, Turkey",
            "The Oculus Transportation Hub" to "New York, United States",
            "Sunset at the Sydney Opera House" to "Sydney, Australia",
            "Sunny Afternoon in Florence, Italy" to "Florence, Italy",
            "Neon City" to "Singapore",
            "Clouds Over The Golden Gate Bridge" to "San Francisco, United States",
            "Bay Bridge over Treasure Island" to "San Francisco, United States",
            "Spinner Dolphins" to "Hawaii, United States",
            "Faroe Islands" to "Faroe Islands",
        )

    // These sources reuse a state/country as their description; identify the clip by its catalog filename prefix.
    private val community =
        mapOf(
            "video_inspire_florida_miami_" to "Miami, United States",
            "video_inspire_nevada_lasvegas_" to "Las Vegas, United States",
            "video_inspire_oregon_lone-ranch-beach_" to "Oregon, United States",
            "video_inspire_california_vineyard_" to "California, United States",
            "video_inspire_italy_como_" to "Cerano d’Intelvi, Italy",
            "video_inspire_oregon_portland_" to "Portland, United States",
            "video_inspire_california_big-sur_" to "California, United States",
            "video_inspire_california_catalina_avalon_" to "Avalon, United States",
            "video_inspire_uk_london_" to "London, United Kingdom",
            "video_inspire_texas_pecos_" to "Pecos, United States",
            "video_inspire_sanfrancisco_" to "San Francisco, United States",
            "video_inspire_alabama_montgomery_" to "Montgomery, United States",
            "video_inspire_california_santa-barbara_" to "Santa Barbara, United States",
            "video_inspire_uk_edinburgh_" to "Edinburgh, United Kingdom",
            "video_inspire_texas_austin_" to "Austin, United States",
            "video_inspire_california_catalina_" to "California, United States",
            "robinfrcd_reunion_capmechant_" to "Réunion, France",
            "robinfrcd_reunion_capmechant2_" to "Réunion, France",
            "robinfrcd_reunion_grandcoude_" to "Réunion, France",
            "robinfrcd_reunion_grandetang_" to "Réunion, France",
            "robinfrcd_reunion_lagon_" to "Réunion, France",
            "robinfrcd_reunion_mafate_" to "Réunion, France",
            "robinfrcd_reunion_pitondesneiges_" to "Réunion, France",
            "robinfrcd_reunion_pitonfournaise_" to "Réunion, France",
            "robinfrcd_reunion_trestastar_" to "Réunion, France",
            "robinfrcd_reunion_troudefer_" to "Réunion, France",
            "robinfrcd_french_alps_" to "Auvergne-Rhône-Alpes, France",
            "robinfrcd_aurland_" to "Aurland, Norway",
            "robinfrcd_hennigsvaer_" to "Henningsvær, Norway",
            "robinfrcd_nusfjord_" to "Nusfjord, Norway",
            "robinfrcd_reine_" to "Reine, Norway",
            "robinfrcd_vettisfossen_" to "Norway",
        )
}
