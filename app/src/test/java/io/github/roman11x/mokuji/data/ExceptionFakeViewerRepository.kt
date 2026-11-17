package io.github.roman11x.mokuji.data

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class ExceptionFakeViewerRepository(
    private val exception: Exception
) : ViewerRepository {
    override suspend fun getViewer(): User {
        val delayValue = 1_000.milliseconds
        delay(delayValue)
        throw exception
    }
}