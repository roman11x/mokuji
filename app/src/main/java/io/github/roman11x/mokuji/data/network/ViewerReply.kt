package io.github.roman11x.mokuji.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This file decodes the response for the following Viewer query:
 * ```
 * {
 *   Viewer {
 *     id       # that account's number on AniList
 *     name     # that account's username
 *   }
 * }
 * ```
 * Defined in AniList's GraphQL schema
 *
 * The response is of the shape:
 * ```
 * {
 *   "data": {
 *     "Viewer": {
 *       "id": 1232456,
 *       "name": "username"
 *     }
 *   }
 * }
 * ```
 */
@Serializable
data class ViewerReply(
    val data: ViewerData?,
    val errors: List<AniListError> = emptyList()
)

@Serializable
data class ViewerData(
    @SerialName("Viewer") val viewer: Viewer?
)

@Serializable
data class Viewer(
    val id: Int,
    val name: String
)