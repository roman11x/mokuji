package io.github.roman11x.mokuji.data

import io.github.roman11x.mokuji.data.auth.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.milliseconds

class FakeViewerRepository(
    private val authRepo: AuthRepository? = null
) : ViewerRepository {
    override suspend fun getViewer(): User {
        if (authRepo != null && authRepo.tokenStream.first() == null) {
            throw InvalidTokenException("Token cannot be null")
        }
        return User(1)
    }
}