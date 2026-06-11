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
            setLanguage(lang)
            _uiState.update {
                it.copy(showLanguageDialog = false, needsRestart = lang != it.language)
            }
        }
    }

    fun onRestartHandled() = _uiState.update { it.copy(needsRestart = false) }

    // ── Notifikasi ────────────────────────────────────────────
    fun onToggleNotification(enabled: Boolean) {
        viewModelScope.launch { setNotificationEnabled(enabled) }
    }
}