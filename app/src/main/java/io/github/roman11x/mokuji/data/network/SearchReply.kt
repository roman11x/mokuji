package io.github.roman11x.mokuji.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * This file serializes the JSON reply from the Anilist API
 */
@Serializable
data class MangaReply(
     val data: SearchData?
)

@Serializable
data class SearchData(
    @SerialName("Page") val page: SearchPage?
)


@Serializable
data class SearchPage(
    val media: List<SearchMedia?>?
)

@Serializable
data class SearchMedia(
    val id: Int,
    val title: Title?,
    val format: String?,
    val startDate: StartDate?,
    val coverImage: CoverImage?
)

@Serializable
data class Title(
    val userPreferred: String?,
    val romaji: String?,
    val english: String?,
    val native: String?
)

@Serializable
data class StartDate(
    val year: Int?
)

@Serializable
data class CoverImage(
    val large: String?
)