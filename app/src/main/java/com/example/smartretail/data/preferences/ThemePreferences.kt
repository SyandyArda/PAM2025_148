package com.example.smartretail.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property untuk DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

object ThemePreferences {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    
    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }
    
    // Save theme preference
    suspend fun saveTheme(context: Context, mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }
    
    // Get theme preference as Flow
    fun getTheme(context: Context): Flow<ThemeMode> {
        return context.dataStore.data.map { preferences ->
            val themeName = preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name
            try {
                ThemeMode.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }
    }
}
