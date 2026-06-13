package com.example.fundflow.feature.settings.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fundflow.core.worker.IuranReminderWorker
import com.example.fundflow.feature.settings.domain.usecase.*
import com.google.firebase.messaging.FirebaseMessaging // IMPORT FCM
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettings: ObserveSettingsUseCase,
    private val setDarkTheme: SetDarkThemeUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val setNotificationEnabled: SetNotificationEnabledUseCase,
    private val firebaseMessaging: FirebaseMessaging, // INJECT FCM YANG SUDAH ADA DI DI MODULE
    @ApplicationContext private val context: Context // INJECT CONTEXT UNTUK WORKMANAGER
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    companion object {
        private const val WORK_NAME_IURAN = "iuran_reminder"
        private const val FCM_TOPIC_GENERAL = "channel_general"
        private const val FCM_TOPIC_IURAN = "channel_iuran_reminder"
    }

    init {
        observeSettings()
            .onEach { settings ->
                _uiState.update {
                    it.copy(
                        isDarkTheme           = settings.isDarkTheme,
                        language              = settings.language,
                        isNotificationEnabled = settings.isNotificationEnabled,
                        isLoading             = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // ── Dark / Light Mode ─────────────────────────────────────
    fun onToggleDarkTheme(enabled: Boolean) {
        viewModelScope.launch { setDarkTheme(enabled) }
    }

    fun onShowThemeDialog()    = _uiState.update { it.copy(showThemeDialog = true) }
    fun onDismissThemeDialog() = _uiState.update { it.copy(showThemeDialog = false) }

    fun onSelectTheme(isDark: Boolean) {
        viewModelScope.launch {
            setDarkTheme(isDark)
            _uiState.update { it.copy(showThemeDialog = false) }
        }
    }

    // ── Bahasa ────────────────────────────────────────────────
    fun onShowLanguageDialog()    = _uiState.update { it.copy(showLanguageDialog = true) }
    fun onDismissLanguageDialog() = _uiState.update { it.copy(showLanguageDialog = false) }

    fun onSelectLanguage(lang: String) {
        viewModelScope.launch {
            val previousLanguage = _uiState.value.language
            setLanguage(lang)
            _uiState.update {
                it.copy(
                    showLanguageDialog = false,
                    needsRestart       = lang != previousLanguage
                )
            }
        }
    }

    fun onRestartHandled() = _uiState.update { it.copy(needsRestart = false) }

    // ── Notifikasi (REVISI BESAR INTEGRASI FCM & WORKMANAGER) ──
    fun onToggleNotification(enabled: Boolean) {
        viewModelScope.launch {
            // 1. Simpan preferensi ke DataStore lokal
            setNotificationEnabled(enabled)

            if (enabled) {
                // 2. KONDISI ON: Aktifkan langganan Cloud Messaging FCM berdasarkan topik channel Anda
                firebaseMessaging.subscribeToTopic(FCM_TOPIC_GENERAL)
                firebaseMessaging.subscribeToTopic(FCM_TOPIC_IURAN)

                // 3. KONDISI ON: Jadwalkan ulang Worker Pengingat Iuran Lokal
                setupPeriodicIuranWorker()
            } else {
                // 4. KONDISI OFF: Batalkan langganan Cloud Messaging FCM agar push notif dari web tidak masuk
                firebaseMessaging.unsubscribeFromTopic(FCM_TOPIC_GENERAL)
                firebaseMessaging.unsubscribeFromTopic(FCM_TOPIC_IURAN)

                // 5. KONDISI OFF: Batalkan antrean WorkManager secara permanen
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_IURAN)
            }
        }
    }

    /**
     * Helper untuk mendaftarkan IuranReminderWorker ke dalam sistem Android secara berkala (1 Hari sekali)
     */
    private fun setupPeriodicIuranWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Worker Anda diset berjalan tanpa internet (baca Room)
            .build()

        val request = PeriodicWorkRequestBuilder<IuranReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_IURAN,
            ExistingPeriodicWorkPolicy.KEEP, // KEEP memastikan jadwal tidak tumpang tindih jika sudah berjalan
            request
        )
    }
}