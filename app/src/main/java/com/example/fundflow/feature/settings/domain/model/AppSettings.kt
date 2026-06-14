package com.example.fundflow.feature.settings.domain.model

data class AppSettings(
    val isDarkTheme: Boolean         = false,
    val language: String              = "id",
    val isNotificationEnabled: Boolean = true
)