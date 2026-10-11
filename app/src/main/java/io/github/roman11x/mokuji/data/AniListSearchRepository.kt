package io.github.roman11x.mokuji.data

import io.github.roman11x.mokuji.data.network.AniListApi
import io.github.roman11x.mokuji.data.network.toManga

/**
 * Repository implementation that fetches manga search results from the AniList API.
 */
class AniListSearchRepository(private val api: AniListApi) : SearchRepository {
    override suspend fun searchManga(query: String): List<Manga> {
        return api.searchManga(query).data
            ?.page
            ?.media // a list of all SearchMedia in the reply
            ?.mapNotNull { it?.toManga() } // Convert each SearchMedia to a Manga model, discarding nulls
            ?: emptyList() // return an empty list if there's no data
    }
}