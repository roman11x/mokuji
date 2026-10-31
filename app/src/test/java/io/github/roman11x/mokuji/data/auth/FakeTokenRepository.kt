package io.github.roman11x.mokuji.data.auth

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake implementation of the [TokenRepository] interface.
 * Used for testing.
 */
class FakeTokenRepository(
    initialToken: String? = null //simulate the token on appStart
) : TokenRepository {
    override val tokenStream = MutableStateFlow(initialToken)

    override suspend fun saveToken(token: String) {
        tokenStream.value = token
    }

    override suspend fun deleteToken() {
        tokenStream.value = null
    }

}