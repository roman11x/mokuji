package io.github.roman11x.mokuji.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.roman11x.mokuji.MokujiApplication
import io.github.roman11x.mokuji.data.auth.TokenRepository
import kotlinx.coroutines.launch
import java.io.IOException
private const val TAG = "Mokuji"
class LoginViewModel(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    fun save(token: String?) {
        if (token == null) return

        Log.d(TAG, "tokenIsBlank=${token.isBlank()}")

        viewModelScope.launch {
            try {
                tokenRepository.saveToken(token)
            } catch (e: IOException) {
                println("write failed")
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