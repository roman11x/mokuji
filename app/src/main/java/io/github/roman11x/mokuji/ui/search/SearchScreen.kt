package io.github.roman11x.mokuji.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.roman11x.mokuji.R
import io.github.roman11x.mokuji.data.Manga

// The screen for the search tab.
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
) {
    val focusManager = LocalFocusManager.current

    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val query by searchViewModel.query.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = searchViewModel::updateQuery,
            singleLine = true,
            label = { Text(text = stringResource(R.string.search_manga)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    searchViewModel.search()
                    focusManager.clearFocus()
                },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        when (val state = uiState) {
            is SearchUiState.Content -> {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),// weight to fill the available space
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(state.mangaList, key = { it.id }) { manga ->
                        CardResult(manga = manga)
                    }
                }
            }
            SearchUiState.Empty -> Text(text = stringResource(R.string.no_results))
            SearchUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            SearchUiState.NoQuery -> Text(text = stringResource(R.string.please_enter_a_query))
        }
    }
}

// map the returned ENUM from the api to a
// user facing String resource
private fun formatRes(format: String?): Int? =
    when (format) {
        "MANGA" -> R.string.manga_format
        "NOVEL" -> R.string.novel_format
        "ONE_SHOT" -> R.string.one_shot_format
        else -> null
    }

/**
 * A card for displaying a manga result.
 * @param manga the manga to display
 * @param modifier the modifier to apply
 */
@Composable
private fun CardResult(manga: Manga, modifier: Modifier = Modifier) {
    val formatResId: Int? = formatRes(manga.format) //The string resource of the manga's format
    val formatText: String? = if (formatResId != null) {
        stringResource(formatResId)
    } else {
        null
    }

    val line: String? = when {
        formatText != null && manga.year != null ->
            stringResource(R.string.format_year, formatText, manga.year)

        formatText != null -> formatText
        manga.year != null -> manga.year.toString()
        else -> null
    }
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = manga.title, style = MaterialTheme.typography.titleLarge)
            if (line != null) {
                Text(text = line, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}



