package io.github.roman11x.mokuji

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.roman11x.mokuji.ui.search.SearchScreen

/**
 * The different tabs in the Mokuji app.
 * Library - The user's manga from the API
 * Search - query the API
 * Stats - show the user's statistics
 */
enum class MokujiTab(
    val labelResID: Int,
    val iconResID: Int
) {
    LIBRARY(R.string.tab_library, R.drawable.menu_book_24px),
    SEARCH(R.string.tab_search, R.drawable.search_24px),
    STATS(R.string.tab_statistics, R.drawable.bar_chart_24px)
}

/**
 * The main Mokuji app.
 */
@Composable
fun MokujiApp(modifier: Modifier = Modifier) {
    var chosen by rememberSaveable { mutableStateOf(MokujiTab.LIBRARY) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                MokujiTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = chosen == tab,
                        onClick = { chosen = tab },
                        icon = {
                            Icon(
                                painter = painterResource(tab.iconResID),
                                contentDescription = null // no need for a description as there are labels
                            )
                        },
                        label = { Text(text = stringResource(id = tab.labelResID)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (chosen) {
            MokujiTab.LIBRARY -> Text(modifier = Modifier.padding(innerPadding), text = "Library")
            MokujiTab.SEARCH -> SearchScreen(modifier = Modifier.padding(innerPadding))
            MokujiTab.STATS -> Text(modifier = Modifier.padding(innerPadding), text = "Statistics")
        }
    }
}