package io.github.roman11x.mokuji.ui.auth

import io.github.roman11x.mokuji.data.auth.FakeTokenRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
        val viewModel = LoginViewModel(FakeTokenRepository())

        // start a background coroutine on the test's clock
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {} // be a subscriber: now stateIn starts reading
        }
        assertEquals(LoginUiState.LoggedOut, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `existing token means logged in`() = runTest {
        val viewModel = LoginViewModel(FakeTokenRepository("token"))

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `saving token changes state to logged in`() = runTest {
        val viewModel = LoginViewModel(FakeTokenRepository())

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.save("token")

        advanceUntilIdle()
        assertEquals(LoginUiState.LoggedIn, viewModel.uiState.value)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}