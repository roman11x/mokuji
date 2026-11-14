package io.github.roman11x.mokuji.data.auth

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake implementation of the [AuthRepository] interface.
 * Used for testing.
 */
class FakeAuthRepository(
    initialToken: String? = null //simulate the token on appStart
) : AuthRepository {
    override val tokenStream = MutableStateFlow(initialToken)

    override suspend fun saveToken(token: String) {
        tokenStream.value = token
    }

    override suspend fun clearAuth() {
        tokenStream.value = null
    }

}