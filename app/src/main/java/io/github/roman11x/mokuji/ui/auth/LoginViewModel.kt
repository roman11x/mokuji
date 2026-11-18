package io.github.roman11x.mokuji.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roman11x.mokuji.MokujiApplication
import io.github.roman11x.mokuji.data.InvalidTokenException
import io.github.roman11x.mokuji.data.ViewerRepository
import io.github.roman11x.mokuji.data.auth.AuthRepository
import io.github.roman11x.mokuji.data.network.AniListException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "Mokuji"

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val viewerRepository: ViewerRepository,
    private val log: (String?, Exception) -> Unit = { msg, ex -> Log.e(TAG, msg, ex) }
) : ViewModel() {
    /**
     * Converts the cold [AuthRepository.tokenStream] into a hot [StateFlow] of [LoginUiState].
     *
     * - `stateIn` exposes a synchronous [.value] property for Compose and shares a single
     *   DataStore collection pipeline among all subscribers.
     * - `initialValue = LoginUiState.Loading` displays a loading state while reading the initial token.
     * - `WhileSubscribed(5_000)` keeps the upstream collection active during brief subscriber drops
     *   (such as screen rotations), while pausing collection 5 seconds after the UI is hidden.
     */

    private var authJob: Job? = null

    init {
        authJob = viewModelScope.launch { // check token validity on startup
            checkToken()
        }
    }

    val uiState: StateFlow<LoginUiState> = authRepository.tokenStream.map { token ->
        if (token == null) LoginUiState.LoggedOut
        else LoginUiState.LoggedIn
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = LoginUiState.Loading
    )

    fun save(token: String?) {
        if (token == null) return

        authJob?.cancel() // cancel any in-flight jobs

        authJob = viewModelScope.launch {
            try {
                authRepository.saveToken(token)
                checkToken() // validate the newly saved token
            } catch (e: IOException) {
                log("Error saving token", e) // v1: failure to write to disk is rare
            }
        }
    }

    fun logout() {
        authJob?.cancel() // cancel any in-flight jobs

        viewModelScope.launch {
            try {
                authRepository.clearAuth() // remove the stored user id and token
            } catch (e: IOException) {
                log("Error clearing auth", e)
            }
        }
    }


    private suspend fun checkToken() {
        try {
            val user = viewerRepository.getViewer()
            try {
                authRepository.saveId(user.id) // save the corresponding user id to the token
            } catch (e: IOException) {
                log("Error saving user id", e)
            }
        } catch (e: IOException) {
            log("Network Error", e)
        } catch (e: AniListException) {
            log(e.message, e)
        } catch (e: InvalidTokenException) { // the token is invalid, delete it so the user can log in again
            try {
                authRepository.clearAuth()
            } catch (ex: IOException) {
                log("error deleting token", ex) // v1: failure to write to disk is rare
            }
        }
    }

    // recipe viewModels() uses to build LoginViewModel
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MokujiApplication
                LoginViewModel(
                    authRepository = application.container.authRepository,
                    viewerRepository = application.container.viewerRepository
                )
            }
        }
    }
}


