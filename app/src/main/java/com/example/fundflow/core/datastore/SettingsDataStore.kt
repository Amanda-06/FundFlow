package com.example.fundflow.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property — satu instance DataStore per Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fundflow_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ── Keys ─────────────────────────────────────────────────
    companion object {
        val KEY_DARK_THEME        = booleanPreferencesKey("dark_theme")
        val KEY_LANGUAGE          = stringPreferencesKey("language")          // "id" | "en"
        val KEY_NOTIFICATION      = booleanPreferencesKey("notification")
        val KEY_HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val KEY_IS_LOGGED_IN      = booleanPreferencesKey("is_logged_in")
    }

    // ── Flows (read) ──────────────────────────────────────────
    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_DARK_THEME] ?: false }

    val language: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_LANGUAGE] ?: "id" }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION] ?: true }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_HAS_SEEN_ONBOARDING] ?: false }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_IS_LOGGED_IN] ?: false }

    // ── Suspend Writers ───────────────────────────────────────
    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DARK_THEME] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs -> prefs[KEY_LANGUAGE] = lang }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_NOTIFICATION] = enabled }
    }

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_HAS_SEEN_ONBOARDING] = seen }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_IS_LOGGED_IN] = loggedIn }
    }

    /** Hapus semua preferensi saat logout */
    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
