package io.github.roman11x.mokuji.data

interface SearchRepository {
    suspend fun searchManga(query: String): List<Manga>
}