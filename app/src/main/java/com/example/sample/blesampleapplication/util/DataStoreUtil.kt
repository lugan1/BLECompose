package com.example.sample.blesampleapplication.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

fun Context.readStringFromDataStore(key: DataStoreKey): Flow<String> {
    val datastoreKey = stringPreferencesKey(key.value)

    return dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[datastoreKey] ?: "" }
}

suspend fun Context.updateStringToDataStore(key: DataStoreKey, value: String) {
    val datastoreKey = stringPreferencesKey(key.value)
    dataStore.edit { preferences -> preferences[datastoreKey] = value }
}
