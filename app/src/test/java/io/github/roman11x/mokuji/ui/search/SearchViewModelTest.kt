package io.github.roman11x.mokuji.ui.search

import androidx.lifecycle.SavedStateHandle
import io.github.roman11x.mokuji.data.FakeSearchRepository
import io.github.roman11x.mokuji.data.Manga
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class SearchViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `a blank submit changes the state to NoQuery`() =
        runTest { // run all cocoroutines on the same clock
            val mangaList = listOf(
                Manga(
                    id = 2,
                    title = "Monster",
                    coverUrl = null,
                    format = "MANGA",
                    year = 1994
                ),
                Manga(
                    id = 3,
                    title = "Monster Musume",
                    coverUrl = null,
                    format = "MANGA",
                    year = 2012
                )
            )
            val viewModel =
                SearchViewModel(
                    SavedStateHandle(),
                    FakeSearchRepository()
                )

            viewModel.updateQuery("monster")
            viewModel.search()
            advanceUntilIdle() // move the fake clock until nothing is waiting: the 1s passes in 0s real time
            assertEquals(
                SearchUiState.Content(mangaList),
                viewModel.uiState.value
            )


            viewModel.updateQuery("")
            viewModel.search()
            // no need to use advanceUntilIdle as we are not entering a cocoroutine
            assertEquals(SearchUiState.NoQuery, viewModel.uiState.value)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `a search with no matches changes the state to Empty`() = runTest {
        val viewModel =
            SearchViewModel(
                SavedStateHandle(),
                FakeSearchRepository()
            )
        viewModel.updateQuery("not a manga")
        viewModel.search()
        advanceUntilIdle()
        assertEquals(SearchUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `search saves the submitted text`() {
        val handle = SavedStateHandle(mapOf("lastSearchedQuery" to ""))

        val viewModel =
            SearchViewModel(
                handle,
                FakeSearchRepository()
            )
        viewModel.updateQuery("monster")
        viewModel.search()
        assertEquals("monster", handle.get<String>("lastSearchedQuery"))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `restores last submitted search after death`() = runTest {
        val mangaList = listOf(
            Manga(
                id = 2,
                title = "Monster",
                coverUrl = null,
                format = "MANGA",
                year = 1994
            ),
            Manga(
                id = 3,
                title = "Monster Musume",
                coverUrl = null,
                format = "MANGA",
                year = 2012
            ),
        )
        val handle = SavedStateHandle(mapOf("lastSearchedQuery" to "monster"))
        val viewModel =
            SearchViewModel(
                handle,
                FakeSearchRepository()
            )
        advanceUntilIdle()
        assertEquals(SearchUiState.Content(mangaList), viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}

