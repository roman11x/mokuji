package io.github.roman11x.mokuji.data

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class FakeSearchRepository : SearchRepository {
    private val fakeMangaList = listOf(
        Manga(
            id = 1,
            title = "One Piece",
            coverUrl = null,
            format = "MANGA",
            year = 1997
        ),
        Manga(
            id = 2,
            title = "Monster",
            coverUrl = null,
            format = "MANGA",
            year = 1994
        ),
        Manga(
            id = 3,
            title = "Monster Musume",
            coverUrl = null,
            format = "MANGA",
            year = 2012
        ),
        Manga(
            id = 4,
            title = "Attack on Titan",
            coverUrl = null,
            format = "MANGA",
            year = 2009
        ),
        Manga(
            id = 5,
            title = "Spy x Family",
            coverUrl = null,
            format = "MANGA",
            year = 2019
        )
    )

    override suspend fun searchManga(query: String): List<Manga> {
        delay(1_000.milliseconds)
        return fakeMangaList.filter { it.title.contains(query, ignoreCase = true) }
    }
}