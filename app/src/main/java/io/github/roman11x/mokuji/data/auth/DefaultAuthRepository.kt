package io.github.roman11x.mokuji.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences          // "key → value pairs", what this store holds
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val AUTH_FILE_NAME = "user_token"
private val TOKEN_KEY = stringPreferencesKey("token_key")
private val ID_KEY = intPreferencesKey("id_key")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_FILE_NAME)

class DefaultAuthRepository(context: Context) : AuthRepository {
    private val dataStore = context.dataStore

    /**
     * Returns a [Flow] that emits the current token.
     * If there's no token it emits null.
     * This transforms the [Preferences] object into a [String?] by mapping the
     * key-value pairs to the specific value associated with [TOKEN_KEY].
     *
     */
    override val tokenStream = dataStore.data.map { prefs -> prefs[TOKEN_KEY] }
    override val idStream = dataStore.data.map { prefs -> prefs[ID_KEY] }

    override suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    override suspend fun saveId(id: Int) {
        dataStore.edit { prefs ->
            prefs[ID_KEY] = id
        }
    }
    /**
     * Clears stored authentication credentials and resets session state.
     */
    override suspend fun clearAuth() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(ID_KEY) // Wipe viewer ID along with session token
        }
    }
}
