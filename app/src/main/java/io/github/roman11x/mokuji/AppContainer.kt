package io.github.roman11x.mokuji

import android.content.Context
import io.github.roman11x.mokuji.data.AniListSearchRepository
import io.github.roman11x.mokuji.data.SearchRepository
import io.github.roman11x.mokuji.data.auth.AniListTokenRepository
import io.github.roman11x.mokuji.data.auth.TokenRepository
import io.github.roman11x.mokuji.data.network.AniListApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates shared objects for the app.
 */
class AppContainer(context: Context) {

    // Creating an HttpClient instance is expensive, so it is initialized once when the process starts.
    private val httpClient = HttpClient(OkHttp) {
        // Enables ContentNegotiation plugin for JSON serialization
        install(ContentNegotiation) {
            json(
                Json {
                    // Ignore JSON fields that don't match any data class properties
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    private val api = AniListApi(httpClient) // Initialize the AniListApi with the HttpClient
    val searchRepository: SearchRepository = AniListSearchRepository(api)
    val tokenRepository: TokenRepository = AniListTokenRepository(context)

}
