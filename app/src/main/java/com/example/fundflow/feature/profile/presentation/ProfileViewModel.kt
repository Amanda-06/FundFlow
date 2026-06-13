package com.example.fundflow.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.auth.domain.usecase.LogoutUseCase
import com.example.fundflow.feature.profile.data.repository.ProfileRepositoryImpl // TAMBAHAN IMPORT
import com.example.fundflow.feature.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val repository: ProfileRepositoryImpl // TAMBAHAN REPOSITORI UNTUK SYNC CLOUD
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        fetchProfileDataDariCloud() // TAMBAHAN CALL SINKRONISASI

        getProfile()
            .onEach { profile -> _uiState.update { it.copy(profile = profile, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    private fun fetchProfileDataDariCloud() {
        viewModelScope.launch {
            try {
                repository.syncWithCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onShowLogoutDialog()    = _uiState.update { it.copy(showLogoutDialog = true) }
    fun onDismissLogoutDialog() = _uiState.update { it.copy(showLogoutDialog = false) }

    fun onConfirmLogout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            logoutUseCase()
            _uiState.update { it.copy(isLoggingOut = false, showLogoutDialog = false) }
            onLoggedOut()
        }
    }
}