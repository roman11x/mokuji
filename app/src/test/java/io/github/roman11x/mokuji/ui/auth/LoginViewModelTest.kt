package io.github.roman11x.mokuji.ui.auth

import io.github.roman11x.mokuji.data.ExceptionFakeViewerRepository
import io.github.roman11x.mokuji.data.FakeViewerRepository
import io.github.roman11x.mokuji.data.InvalidTokenException
import io.github.roman11x.mokuji.data.auth.FakeAuthRepository
import io.github.roman11x.mokuji.data.network.AniListException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `empty token means logged out`() = runTest {
        // the viewmodel's uiState uses stateIn(WhileSubscribed)
        // so we need to collect the uiState
        val viewModel = LoginViewModel(
            FakeAuthRepository(),
            FakeViewerRepository()
        )

        // start a background coroutine on the test's clock
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {} // be a subscriber: now stateIn starts reading
        }
        assertEquals(LoginUiState.LoggedOut, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `existing token means logged in`() = runTest {
        val viewModel = LoginViewModel(
            FakeAuthRepository("token", 1),
            FakeViewerRepository()
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saving token changes state to logged in`() = runTest {
        val viewModel = LoginViewModel(
            FakeAuthRepository(),
            FakeViewerRepository()
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.save("token")

        advanceUntilIdle()

        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saving a null token writes nothing`() = runTest {
        val viewModel = LoginViewModel(
            FakeAuthRepository(),
            FakeViewerRepository()
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.save(null)

        advanceUntilIdle()

        assertEquals(LoginUiState.LoggedOut, viewModel.uiState.value)
    }

    @Test
    fun `before file delivers the state is loading`() {
        val viewModel = LoginViewModel(
            FakeAuthRepository(),
            FakeViewerRepository()
        )
        assertEquals(LoginUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `id for existing token is saved`() = runTest {
        val authRepo = FakeAuthRepository("token")

        LoginViewModel(
            authRepo,
            FakeViewerRepository()
        )

        assertEquals(1, authRepo.idStream.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `rejected existing token means auth cleared`() = runTest {
        val authRepo = FakeAuthRepository("invalid token", 3)

        val viewModel = LoginViewModel(
            authRepo,
            ExceptionFakeViewerRepository(
                InvalidTokenException("Invalid Token")
            )
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertNull(authRepo.tokenStream.first())
        assertNull(authRepo.idStream.first())
        assertEquals(LoginUiState.LoggedOut, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `existing token with no internet means loggedIn`() = runTest {
        val authRepo = FakeAuthRepository("token", 3)
        val networkError = IOException()
        val loggedErrors = mutableListOf<Exception>()

        val viewModel = LoginViewModel(
            authRepo,
            ExceptionFakeViewerRepository(networkError),
            log = {_, ex -> loggedErrors.add(ex)}
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
        assertEquals("token", authRepo.tokenStream.first())
        assertEquals(3, authRepo.idStream.first())
        assertEquals(listOf(networkError), loggedErrors)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `existing token with AniList error means loggedIn`() = runTest {
        val authRepo = FakeAuthRepository("token", 3)
        val aniListError = AniListException("AniList Error")
        val loggedErrors = mutableListOf<Exception>()

        val viewModel = LoginViewModel(
            authRepo,
            ExceptionFakeViewerRepository(aniListError),
            log = {_, ex -> loggedErrors.add(ex)}
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
        assertEquals("token", authRepo.tokenStream.first())
        assertEquals(3, authRepo.idStream.first())
        assertEquals(listOf(aniListError), loggedErrors)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saving token fetches and saves user id`() = runTest {
        val authRepo = FakeAuthRepository()

        val viewModel = LoginViewModel(
            authRepo,
            FakeViewerRepository(authRepo),
            log = {_, _ -> }
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.save("token")

        advanceUntilIdle()

        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
        assertEquals("token", authRepo.tokenStream.first())
        assertEquals(1, authRepo.idStream.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}