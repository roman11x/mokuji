package io.github.roman11x.mokuji.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences          // "key → value pairs", what this store holds
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val TOKEN_FILE_NAME = "user_token"
private val TOKEN_KEY = stringPreferencesKey("token_key")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_FILE_NAME)

class TokenRepository(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}