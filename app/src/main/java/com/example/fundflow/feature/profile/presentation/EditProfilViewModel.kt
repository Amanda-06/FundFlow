// feature/profile/presentation/EditProfilViewModel.kt

package com.example.fundflow.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.usecase.GetProfileUseCase
import com.example.fundflow.feature.profile.domain.usecase.UpdatePasswordUseCase
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
    private val updateProfile: UpdateProfileUseCase,
    private val updatePassword: UpdatePasswordUseCase       // [BARU]
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

    // ── Handler field profil ─────────────────────────────────
    fun onNamaLengkapChange(v: String)    = _uiState.update { it.copy(namaLengkap = v, namaLengkapError = null) }
    fun onUsernameChange(v: String)       = _uiState.update { it.copy(username = v, usernameError = null) }
    fun onNamaOrganisasiChange(v: String) = _uiState.update { it.copy(namaOrganisasi = v) }

    // ── Handler field password ────────────────────────────────
    fun onPasswordSaatIniChange(v: String)    = _uiState.update { it.copy(passwordSaatIni = v, passwordSaatIniError = null) }
    fun onPasswordBaruChange(v: String)       = _uiState.update { it.copy(passwordBaru = v, passwordBaruError = null) }
    fun onKonfirmasiPasswordChange(v: String) = _uiState.update { it.copy(konfirmasiPassword = v, konfirmasiPasswordError = null) }

    fun onTogglePasswordSaatIniVisible()    = _uiState.update { it.copy(passwordSaatIniVisible = !it.passwordSaatIniVisible) }
    fun onTogglePasswordBaruVisible()       = _uiState.update { it.copy(passwordBaruVisible = !it.passwordBaruVisible) }
    fun onToggleKonfirmasiPasswordVisible() = _uiState.update { it.copy(konfirmasiPasswordVisible = !it.konfirmasiPasswordVisible) }

    // ── Simpan semua perubahan ────────────────────────────────
    fun onSave() {
        if (!validateProfil()) return

        val passwordDiisi = _uiState.value.passwordSaatIni.isNotBlank()
                || _uiState.value.passwordBaru.isNotBlank()
                || _uiState.value.konfirmasiPassword.isNotBlank()

        if (passwordDiisi && !validatePassword()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val s = _uiState.value

                // 1. Simpan perubahan data profil
                updateProfile(
                    Profile(
                        userId         = s.userId,
                        namaLengkap    = s.namaLengkap,
                        username       = s.username,
                        email          = s.email,
                        namaOrganisasi = s.namaOrganisasi
                    )
                )

                // 2. Jika ada isian password, update password juga
                if (passwordDiisi) {
                    updatePassword(s.passwordSaatIni, s.passwordBaru)
                }

                _uiState.update { it.copy(isSaving = false, isSuccess = true) }

            } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                // Password lama salah
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        passwordSaatIniError = "Password saat ini salah"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    // ── Validasi ─────────────────────────────────────────────

    /** Validasi field nama & username. Return true jika valid. */
    private fun validateProfil(): Boolean {
        val s      = _uiState.value
        val namaErr = if (s.namaLengkap.isBlank()) "Nama lengkap tidak boleh kosong" else null
        val userErr = when {
            s.username.isBlank()         -> "Username tidak boleh kosong"
            s.username.trim().length < 3 -> "Username minimal 3 karakter"
            else                         -> null
        }
        return if (namaErr != null || userErr != null) {
            _uiState.update { it.copy(namaLengkapError = namaErr, usernameError = userErr) }
            false
        } else true
    }

    /**
     * Validasi section password. Dipanggil hanya jika salah satu field password tidak kosong.
     * Return true jika valid.
     */
    private fun validatePassword(): Boolean {
        val s = _uiState.value
        val saatIniErr = if (s.passwordSaatIni.isBlank()) "Password saat ini tidak boleh kosong" else null
        val baruErr    = when {
            s.passwordBaru.isBlank()    -> "Password baru tidak boleh kosong"
            s.passwordBaru.length < 6   -> "Password baru minimal 6 karakter"
            else                        -> null
        }
        val konfirmasiErr = when {
            s.konfirmasiPassword.isBlank()          -> "Konfirmasi password tidak boleh kosong"
            s.konfirmasiPassword != s.passwordBaru  -> "Konfirmasi password tidak cocok"
            else                                    -> null
        }
        return if (saatIniErr != null || baruErr != null || konfirmasiErr != null) {
            _uiState.update {
                it.copy(
                    passwordSaatIniError    = saatIniErr,
                    passwordBaruError       = baruErr,
                    konfirmasiPasswordError = konfirmasiErr
                )
            }
            false
        } else true
    }

    fun resetSuccess() = _uiState.update { it.copy(isSuccess = false) }
    fun clearError()   = _uiState.update { it.copy(errorMessage = null) }
}