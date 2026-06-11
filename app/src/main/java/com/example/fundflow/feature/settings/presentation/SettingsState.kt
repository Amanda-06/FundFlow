package com.example.fundflow.feature.settings.presentation

data class SettingsState(
    val isDarkTheme: Boolean          = false,
    val language: String              = "id",
    val isNotificationEnabled: Boolean = true,
    val isLoading: Boolean            = true,

    val showLanguageDialog: Boolean   = false,
    val showThemeDialog: Boolean      = false,

    // Restart activity diperlukan saat ganti bahasa
    val needsRestart: Boolean         = false
)