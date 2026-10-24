package io.github.roman11x.mokuji.ui.auth

/**
 * This state drives the switch between the login and the app's tabs
 */
sealed interface LoginUiState{
    /**
     * The state when no user is currently authenticated.
     */
    object LoggedOut: LoginUiState

    /**
     * The state after a successful login attempt, or when the app starts
     * with an existing authentication token.
     */
    object LoggedIn: LoginUiState

    /**
     * The initial state while the system is waiting for the data stream (e.g., DataStore
     * preferences) to emit the current authentication status.
     */
    object Loading: LoginUiState
}
