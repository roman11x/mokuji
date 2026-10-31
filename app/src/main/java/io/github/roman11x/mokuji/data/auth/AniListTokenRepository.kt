package io.github.roman11x.mokuji.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences          // "key → value pairs", what this store holds
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TOKEN_FILE_NAME = "user_token"
private val TOKEN_KEY = stringPreferencesKey("token_key")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_FILE_NAME)

class AniListTokenRepository(context: Context): TokenRepository {
    private val dataStore = context.dataStore
    /**
     * Returns a [Flow] that emits the current token.
     * If there's no token it emits null.
     * This transforms the [Preferences] object into a [String?] by mapping the
     * key-value pairs to the specific value associated with [TOKEN_KEY].
     *
     */
    override val tokenStream = dataStore.data.map { prefs -> prefs[TOKEN_KEY] }

    override suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
