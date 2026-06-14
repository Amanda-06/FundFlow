package com.example.fundflow.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppStartState(
    val hasSeenOnboarding: Boolean = false,
    val isLoggedIn: Boolean        = false,
    val isLoading: Boolean         = true
)

@HiltViewModel
class AppViewModel @Inject constructor(
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    val startState: StateFlow<AppStartState> = combine(
        settingsDataStore.hasSeenOnboarding,
        settingsDataStore.isLoggedIn
    ) { hasSeenOnboarding, isLoggedIn ->
        AppStartState(
            hasSeenOnboarding = hasSeenOnboarding,
            isLoggedIn        = isLoggedIn,
            isLoading         = false
        )
    }.stateIn(
        scope          = viewModelScope,
        started        = SharingStarted.WhileSubscribed(5000),
        initialValue   = AppStartState()
    )
}