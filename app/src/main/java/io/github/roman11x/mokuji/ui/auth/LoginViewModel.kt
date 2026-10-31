package io.github.roman11x.mokuji.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roman11x.mokuji.MokujiApplication
import io.github.roman11x.mokuji.data.auth.DefaultTokenRepository
import io.github.roman11x.mokuji.data.auth.TokenRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "Mokuji"

class LoginViewModel(
    private val tokenRepository: TokenRepository
) : ViewModel() {
    /**
     * Converts the cold [TokenRepository.tokenStream] into a hot [StateFlow] of [LoginUiState].
     *
     * - `stateIn` exposes a synchronous [.value] property for Compose and shares a single
     *   DataStore collection pipeline among all subscribers.
     * - `initialValue = LoginUiState.Loading` displays a loading state while reading the initial token.
     * - `WhileSubscribed(5_000)` keeps the upstream collection active during brief subscriber drops
     *   (such as screen rotations), while pausing collection 5 seconds after the UI is hidden.
     */

    val uiState: StateFlow<LoginUiState> = tokenRepository.tokenStream.map { token ->
        if (token == null) LoginUiState.LoggedOut
        else LoginUiState.LoggedIn
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = LoginUiState.Loading
    )

    fun save(token: String?) {
        if (token == null) return

        viewModelScope.launch {
            try {
                tokenRepository.saveToken(token)
            } catch (e: IOException) {
                Log.e(TAG, "Error saving token", e) // v1: failure to write to disk is rare
            }
        }
    }

    // recipe viewModels() uses to build LoginViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MokujiApplication
                LoginViewModel(
                    tokenRepository = application.container.tokenRepository
                )
            }
        }
    }
}


