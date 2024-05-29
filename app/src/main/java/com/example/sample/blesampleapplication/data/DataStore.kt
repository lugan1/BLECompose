package com.example.sample.blesampleapplication.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

enum class DataStoreKey(val value: String) {
    MAC_ADDRESS("macAddress");
}

class DataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
    }

    fun readString(key: DataStoreKey): Flow<String> {
        val datastoreKey = stringPreferencesKey(key.value)

        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences -> preferences[datastoreKey] ?: "" }
    }

    suspend fun saveString(key: DataStoreKey, value: String) {
        val datastoreKey = stringPreferencesKey(key.value)
        context.dataStore.edit { preferences -> preferences[datastoreKey] = value }
    }
}