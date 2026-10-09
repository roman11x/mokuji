package io.github.roman11x.mokuji.ui.search

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.roman11x.mokuji.R

// The screen for the search tab.
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val query by searchViewModel.query.collectAsStateWithLifecycle()

    TextField(
        value = query,
        onValueChange = searchViewModel::updateQuery,
        singleLine = true,
        label = { Text(text = stringResource(R.string.search_manga)) },
        modifier = modifier
    )
}
