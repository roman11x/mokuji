package io.github.roman11x.mokuji

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.browser.auth.AuthTabIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import io.github.roman11x.mokuji.ui.auth.LoginViewModel
import io.github.roman11x.mokuji.data.auth.parseToken
import io.github.roman11x.mokuji.ui.theme.MokujiTheme

private const val MOKUJI_CLIENT_ID = "54107"


class MainActivity : ComponentActivity() {
    // inject the LoginViewModel
    private val loginViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    // A property initializer runs while the activity object is created, before onCreate
    // register the login activity launcher
    private val loginLauncher = AuthTabIntent.registerActivityResultLauncher(this) { result ->
        if (result.resultCode == AuthTabIntent.RESULT_OK) {
            val token = parseToken(result.resultUri?.fragment)
            loginViewModel.save(token)
        }
    }

    // runs when the login button is clicked
    private fun openLogin() {
        val loginUrl =
            "https://anilist.co/api/v2/oauth/authorize?client_id=$MOKUJI_CLIENT_ID&response_type=token".toUri()
        val authTabIntent =
            AuthTabIntent.Builder().build() //the "open a login tab" request, default settings
        authTabIntent.launch(
            loginLauncher,
            loginUrl,
            "mokuji"
        ) // launch the login tab, mokuji - the redirect scheme to watch for
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // handle cold start
        handleAuthRedirect(intent)
        /**
         * Handles incoming deep link intents for browsers without native AuthTab support (e.g. Firefox),
         * which deliver the OAuth redirect URI via a new intent.
         */
        addOnNewIntentListener { newIntent ->
            handleAuthRedirect(newIntent)
        }
        enableEdgeToEdge()
        setContent {
            MokujiTheme {
                MokujiApp(
                    modifier = Modifier.fillMaxSize(),
                    onLogin = { openLogin() })
            }
        }
    }

    // helper function for handling intent
    // if the process is alive the intent arrives via the listener
    // if Android kills the app, the intent arrives via the starting intent
    private fun handleAuthRedirect(incomingIntent: Intent?) {
        val uri = incomingIntent?.data

        if (uri != null) {
            val token = parseToken(uri.fragment)
            loginViewModel.save(token)
        }
    }
}






