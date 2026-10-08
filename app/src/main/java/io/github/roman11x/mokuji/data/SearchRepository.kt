package io.github.roman11x.mokuji.data

/**
 * The repository for the search feature.
 */
interface SearchRepository {
    suspend fun searchManga(query: String): List<Manga>
}