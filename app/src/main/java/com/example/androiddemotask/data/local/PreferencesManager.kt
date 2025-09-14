package com.example.androiddemotask.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val API_KEY = stringPreferencesKey("api_key")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    suspend fun saveApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    fun getApiKey(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[API_KEY]
        }
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }

    fun getUserName(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME]
        }
    }
}
