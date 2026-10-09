package io.github.roman11x.mokuji

import io.github.roman11x.mokuji.data.FakeSearchRepository
import io.github.roman11x.mokuji.data.SearchRepository

/**
 * Creates shared objects for the app.
 */
class AppContainer {
    val searchRepository: SearchRepository = FakeSearchRepository()
}