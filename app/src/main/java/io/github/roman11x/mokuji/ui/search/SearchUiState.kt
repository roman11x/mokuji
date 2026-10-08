package io.github.roman11x.mokuji.ui.search

import io.github.roman11x.mokuji.data.Manga

/**
 * The state of the search feature.
 * NoQuery - no query has been entered
 * Loading - the search is in progress
 * Empty - the search returned no results
 * Content - the search returned results
 */
sealed interface SearchUiState {
    object NoQuery: SearchUiState
    object Loading: SearchUiState
    object Empty: SearchUiState
    data class Content(val mangaList: List<Manga>): SearchUiState
}
