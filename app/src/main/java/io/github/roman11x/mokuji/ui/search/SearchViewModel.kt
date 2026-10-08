package io.github.roman11x.mokuji.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.roman11x.mokuji.data.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    val query: StateFlow<String> = savedStateHandle.getStateFlow("query", "")

    /**
     * @param newQuery the query to search for
     */
    fun updateQuery(newQuery: String) {
        savedStateHandle["query"] = newQuery
    }

    fun search() {
        val currentQuery = query.value
        if (currentQuery.isBlank()) {
            _uiState.value = SearchUiState.NoQuery
            return
        }
        _uiState.value = SearchUiState.Loading
        viewModelScope.launch {
            val mangaList = searchRepository.searchManga(currentQuery)

            _uiState.value = if (mangaList.isEmpty()) {
                SearchUiState.Empty
            } else {
                SearchUiState.Content(mangaList)
            }
        }
    }
}
