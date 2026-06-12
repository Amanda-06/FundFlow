package com.example.fundflow.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.settings.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeSettings: ObserveSettingsUseCase,
    private val setDarkTheme: SetDarkThemeUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val setNotificationEnabled: SetNotificationEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

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
            // FIX BUG 1:
            // Ambil bahasa SEBELUMNYA dari state saat ini, sebelum apapun diupdate.
            // Ini harus dilakukan di sini (sebelum suspend call), bukan di dalam
            // lambda it.copy() yang dieksekusi nanti — karena pada saat lambda
            // dieksekusi, state bisa saja sudah berubah oleh observer DataStore.
            val previousLanguage = _uiState.value.language

            // Simpan ke DataStore (suspend — menunggu selesai)
            setLanguage(lang)

            // Setelah DataStore selesai diupdate, tentukan apakah perlu restart.
            // Bandingkan lang dengan previousLanguage (yang kita ambil di atas),
            // BUKAN dengan it.language dari lambda (yang sudah bisa berubah).
            _uiState.update {
                it.copy(
                    showLanguageDialog = false,
                    needsRestart       = lang != previousLanguage
                )
            }
        }
    }

    fun onRestartHandled() = _uiState.update { it.copy(needsRestart = false) }

    // ── Notifikasi ────────────────────────────────────────────
    fun onToggleNotification(enabled: Boolean) {
        viewModelScope.launch { setNotificationEnabled(enabled) }
    }
}