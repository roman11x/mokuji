package io.github.roman11x.mokuji

import io.github.roman11x.mokuji.data.FakeSearchRepository
import io.github.roman11x.mokuji.data.SearchRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Creates shared objects for the app.
 */
class AppContainer {
    val searchRepository: SearchRepository = FakeSearchRepository()

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
}
