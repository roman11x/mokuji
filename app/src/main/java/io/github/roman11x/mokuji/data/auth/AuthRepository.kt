package io.github.roman11x.mokuji.data.auth

import kotlinx.coroutines.flow.Flow

/**
 * Represents a repository for storing and retrieving user authentication tokens and ids.
 */
interface AuthRepository {
    val tokenStream: Flow<String?> // emits the current token or null if none is set
    val idStream: Flow<Int?>
    suspend fun saveToken(token: String)
    suspend fun saveId(id: Int)
    suspend fun clearAuth()
}
