package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rise_again_settings")

class SettingsManager(private val context: Context) {

    companion object {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val EVALUATION_TIME = stringPreferencesKey("evaluation_time")
        val CONSECUTIVE_LOW_DAYS = intPreferencesKey("consecutive_low_days")
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[NOTIFICATIONS_ENABLED] ?: true }

    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_THEME_ENABLED] ?: true }

    val evaluationTime: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[EVALUATION_TIME] ?: "22:00" }

    val consecutiveLowDays: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[CONSECUTIVE_LOW_DAYS] ?: 0 }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = enabled
        }
    }

    suspend fun setEvaluationTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[EVALUATION_TIME] = time
        }
    }

    suspend fun incrementConsecutiveLowDays() {
        context.dataStore.edit { preferences ->
            val current = preferences[CONSECUTIVE_LOW_DAYS] ?: 0
            preferences[CONSECUTIVE_LOW_DAYS] = current + 1
        }
    }

    suspend fun resetConsecutiveLowDays() {
        context.dataStore.edit { preferences ->
            preferences[CONSECUTIVE_LOW_DAYS] = 0
        }
    }
}
