package com.example.smartretail.data.local.datastore

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

// Bikin file penyimpanan kecil bernama "retail_session"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "retail_session")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // KUNCI RAHASIA untuk menyimpan data
    private val userTokenKey = stringPreferencesKey("user_token") // Bisa UUID user
    private val userNameKey = stringPreferencesKey("user_name") // Nama User

    // 1. FUNGSI SIMPAN LOGIN (Dipanggil saat tombol Login ditekan)
    suspend fun saveSession(userId: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[userTokenKey] = userId
            preferences[userNameKey] = name
        }
    }

    // 2. FUNGSI CEK LOGIN (Dipanggil saat aplikasi baru dibuka)
    // Flow<String?> artinya datanya mengalir terus (Realtime)
    val getUserToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[userTokenKey]
    }

    val getUserName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[userNameKey]
    }

    // 3. FUNGSI LOGOUT (Hapus ingatan)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
