package com.example.fundflow.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.usecase.GetProfileUseCase
import com.example.fundflow.feature.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfilViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfilState())
    val uiState: StateFlow<EditProfilState> = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = getProfile.once()
            if (profile != null) {
                _uiState.update {
                    it.copy(
                        userId         = profile.userId,
                        namaLengkap    = profile.namaLengkap,
                        username       = profile.username,
                        email          = profile.email,
                        namaOrganisasi = profile.namaOrganisasi,
                        isLoading      = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNamaLengkapChange(v: String)    = _uiState.update { it.copy(namaLengkap = v, namaLengkapError = null) }
    fun onUsernameChange(v: String)       = _uiState.update { it.copy(username = v, usernameError = null) }
    fun onNamaOrganisasiChange(v: String) = _uiState.update { it.copy(namaOrganisasi = v) }

    fun onSave() {
        val s = _uiState.value
        val namaErr = if (s.namaLengkap.isBlank()) "Nama lengkap tidak boleh kosong" else null
        val userErr = if (s.username.isBlank()) "Username tidak boleh kosong"
        else if (s.username.trim().length < 3) "Username minimal 3 karakter" else null

        if (namaErr != null || userErr != null) {
            _uiState.update { it.copy(namaLengkapError = namaErr, usernameError = userErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                updateProfile(
                    Profile(
                        userId         = s.userId,
                        namaLengkap    = s.namaLengkap,
                        username       = s.username,
                        email          = s.email,
                        namaOrganisasi = s.namaOrganisasi
                    )
                )
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
    fun clearError()   = _uiState.update { it.copy(errorMessage = null) }
}