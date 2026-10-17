package io.github.roman11x.mokuji.ui.search

import androidx.lifecycle.SavedStateHandle
import io.github.roman11x.mokuji.R
import io.github.roman11x.mokuji.data.DelayedFakeSearchRepository
import io.github.roman11x.mokuji.data.ExceptionFakeSearchRepository
import io.github.roman11x.mokuji.data.FakeSearchRepository
import io.github.roman11x.mokuji.data.Manga
import io.github.roman11x.mokuji.data.network.AniListException
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
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds


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
    @Test
    fun `submitting blank query while search is in flight updates state to NoQuery`() = runTest {
        val viewModel =
            SearchViewModel(
                SavedStateHandle(),
                FakeSearchRepository()
            )
        viewModel.updateQuery("monster")
        viewModel.search()

        viewModel.updateQuery("")
        viewModel.search()

        advanceUntilIdle() //simulate cocoroutine response after blank search was submitted.
        assertEquals(SearchUiState.NoQuery, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `sequential searches show last result`() = runTest {
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
                DelayedFakeSearchRepository(
                    mapOf(
                        "one" to 2000.milliseconds,
                        "mon" to 1000.milliseconds
                    )
                )
            )
        viewModel.updateQuery("one")
        viewModel.search()
        viewModel.updateQuery("mon")
        viewModel.search()

        advanceUntilIdle()

        assertEquals(SearchUiState.Content(mangaList), viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState changes to network error`() = runTest {
        val viewModel =
            SearchViewModel(
                SavedStateHandle(),
                ExceptionFakeSearchRepository(IOException())
            )

        viewModel.updateQuery("monster")
        viewModel.search()

        advanceUntilIdle()

        assertEquals(SearchUiState.Error(R.string.network_error), viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `uiState changes to AniList error`() = runTest {
        val viewModel =
            SearchViewModel(
                SavedStateHandle(),
                ExceptionFakeSearchRepository(AniListException("error"))
            )

        viewModel.updateQuery("monster")
        viewModel.search()

        advanceUntilIdle()

        assertEquals(SearchUiState.Error(R.string.anilist_error), viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

