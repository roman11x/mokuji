package io.github.roman11x.mokuji.data

import io.github.roman11x.mokuji.data.auth.TokenRepository
import io.github.roman11x.mokuji.data.network.AniListApi
import io.github.roman11x.mokuji.data.network.AniListException
import io.github.roman11x.mokuji.data.network.ViewerReply
import io.github.roman11x.mokuji.data.network.toUser
import kotlinx.coroutines.flow.first

class InvalidTokenException(message: String) : Exception(message)

/**
 * Repository implementation that fetches viewer query results from the AniList API.
 */

class DefaultViewerRepository(
    private val api: AniListApi,
    private val tokenRepository: TokenRepository
) : ViewerRepository {
    override suspend fun getViewer(): User {
        val token =
            tokenRepository.tokenStream.first() ?: throw InvalidTokenException("No token is saved")

        val reply: ViewerReply = try {
            api.fetchViewer(token)
        } catch (e: AniListException) {
            if (e.message?.contains("Invalid token") ?: throw e) {
                throw InvalidTokenException("Invalid token")
            } else throw e
        }

        val user = reply.data
            ?.viewer
            ?.toUser()
            ?: throw AniListException("No viewer found")

        return user
    }
}