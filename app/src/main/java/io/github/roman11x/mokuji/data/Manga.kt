package io.github.roman11x.mokuji.data

/**
This class represents the manga we present to the ui.
 *
 * @property id the Anilist id of the manga
 * @property title the title of the manga, we use  the userPreferred type
 * @property coverUrl the url of the cover image, Coil takes this url and loads the image
 * @property format the format of the manga
 * @property year the year the manga was released, comes from the startDate field of the Anilist API
 */
data class Manga (
    val id: Int,
    val title: String,
    val coverUrl: String?,
    val format: String?,
    val year: Int?
)