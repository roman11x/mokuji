package io.github.roman11x.mokuji.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.roman11x.mokuji.data.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val KEY_QUERY = "query"
private const val KEY_LAST_SEARCHED_QUERY = "lastSearchedQuery"

/**
 * The view model for the search feature.
 * @param savedStateHandle the saved state handle
 * @param searchRepository the repository for the search feature
 */
class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.NoQuery)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    // On startup or process restoration, reload results for the last executed query.
    // Defaults to an empty string if no query was previously executed.
    init {
        val lastSearchedQuery = savedStateHandle.get<String>(KEY_LAST_SEARCHED_QUERY) ?: ""
        loadMangas(lastSearchedQuery)
    }

    /**
     * Updates the search query text input.
     *
     * @param newQuery The new query text entered by the user.
     */
    fun updateQuery(newQuery: String) {
        savedStateHandle[KEY_QUERY] = newQuery
    }

    /**
     * Triggers a search for the current query text.
     *
     * Persists the query to [SavedStateHandle] so results can be restored after process death.
     */
    fun search() {
        val currentQuery = query.value
        // Save last searched query so result can be restored after process death
        savedStateHandle[KEY_LAST_SEARCHED_QUERY] = currentQuery
        loadMangas(currentQuery)
    }

    /**
     * Fetches manga results for [searchText] and updates [_uiState]
     */
    private fun loadMangas(searchText: String) {
        if (searchText.isBlank()) {
            _uiState.value = SearchUiState.NoQuery
            return
        }
        _uiState.value = SearchUiState.Loading
        viewModelScope.launch {
            val mangaList = searchRepository.searchManga(searchText)

            _uiState.value = if (mangaList.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Content(mangaList)
            }
        }
    }
}
