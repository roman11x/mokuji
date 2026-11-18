package io.github.roman11x.mokuji.data.auth

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A fake implementation of the [AuthRepository] interface.
 * Used for testing.
 */
class FakeAuthRepository(
    initialToken: String? = null, //simulate the token on appStart
    initialId: Int? = null, //simulate ID on appStart
    private val clearAuthException: Exception? = null // simulate exception thrown on clear
) : AuthRepository {
    override val tokenStream = MutableStateFlow(initialToken)
    override val idStream = MutableStateFlow(initialId)

    override suspend fun saveToken(token: String) {
        tokenStream.value = token
    }

    override suspend fun saveId(id: Int) {
        idStream.value = id
    }

    override suspend fun clearAuth() {
        if (clearAuthException != null) throw clearAuthException

        tokenStream.value = null
        idStream.value = null
    }

}