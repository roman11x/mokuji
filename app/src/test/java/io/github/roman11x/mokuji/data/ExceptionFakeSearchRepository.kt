package io.github.roman11x.mokuji.data

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

// fake repository for exception tests
class ExceptionFakeSearchRepository(
    private val exception: Exception
): SearchRepository {
    override suspend fun searchManga(query: String): List<Manga> {
        delay(1_000.milliseconds)
        throw exception
    }
}