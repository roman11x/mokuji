package io.github.roman11x.mokuji.data.auth

import kotlinx.coroutines.flow.Flow

/**
 * Represents a repository for storing and retrieving user authentication tokens.
 */
interface TokenRepository {
    val tokenStream: Flow<String?> // emits the current token or null if none is set
    suspend fun saveToken(token: String)
    suspend fun deleteToken()
}
