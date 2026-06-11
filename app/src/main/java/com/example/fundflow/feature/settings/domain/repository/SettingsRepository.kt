package com.example.fundflow.feature.settings.domain.repository

import com.example.fundflow.feature.settings.domain.model.AppSettings
import com.example.fundflow.feature.settings.domain.model.PeriodeKas
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // App preferences (DataStore)
    fun observeSettings(): Flow<AppSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setLanguage(lang: String)
    suspend fun setNotificationEnabled(enabled: Boolean)

    // Periode kas (Room)
    fun observePeriode(userId: String): Flow<PeriodeKas?>
    suspend fun getPeriode(userId: String): PeriodeKas?
    suspend fun updatePeriode(periode: PeriodeKas)
}