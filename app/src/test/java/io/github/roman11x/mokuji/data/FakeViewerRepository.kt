package io.github.roman11x.mokuji.data

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class FakeViewerRepository : ViewerRepository {
    override suspend fun getViewer(): User {
        return User(1)
    }
}