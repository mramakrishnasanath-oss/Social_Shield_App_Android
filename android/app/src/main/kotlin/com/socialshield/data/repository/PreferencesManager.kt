package com.socialshield.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_pref")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LOCAL_PROCESSING = booleanPreferencesKey("local_processing")
        val AUTO_SAVE_SCANS = booleanPreferencesKey("auto_save_scans")
        val THREAT_ALERTS = booleanPreferencesKey("threat_alerts")
    }

    val darkModeFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: true
        }

    val localProcessingFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.LOCAL_PROCESSING] ?: false
        }

    val autoSaveScansFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_SCANS] ?: true
        }

    val threatAlertsFlow: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.THREAT_ALERTS] ?: true
        }

    suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = isDark
        }
    }

    suspend fun setLocalProcessing(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCAL_PROCESSING] = enabled
        }
    }

    suspend fun setAutoSaveScans(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_SCANS] = enabled
        }
    }

    suspend fun setThreatAlerts(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THREAT_ALERTS] = enabled
        }
    }
}
