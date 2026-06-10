// ============================================================
// feature/auth/presentation/register/RegisterViewModel.kt
// ============================================================
package com.example.fundflow.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.auth.domain.usecase.RegisterUseCase
import com.example.fundflow.feature.auth.domain.usecase.ValidateInputUseCase
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.iuran.data.model.PeriodeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validateInput: ValidateInputUseCase,
    private val periodeDao: PeriodeDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    // ── Step 1 input handlers ────────────────────────────────
    fun onNamaLengkapChange(v: String)       = _uiState.update { it.copy(namaLengkap = v, namaLengkapError = null) }
    fun onEmailChange(v: String)             = _uiState.update { it.copy(email = v, emailError = null) }
    fun onUsernameChange(v: String)          = _uiState.update { it.copy(username = v, usernameError = null) }
    fun onPasswordChange(v: String)          = _uiState.update { it.copy(password = v, passwordError = null) }
    fun onConfirmPasswordChange(v: String)   = _uiState.update { it.copy(confirmPassword = v, confirmPasswordError = null) }

    // ── Step 2 input handlers ────────────────────────────────
    fun onNamaOrganisasiChange(v: String)    = _uiState.update { it.copy(namaOrganisasi = v, namaOrganisasiError = null) }
    fun onPeriodeMulaiChange(v: String)      = _uiState.update { it.copy(periodeMulai = v, periodeMulaiError = null) }
    fun onPeriodeSelesaiChange(v: String)    = _uiState.update { it.copy(periodeSelesai = v, periodeSelesaiError = null) }

    /** Validasi Step 1 — kembalikan true jika valid */
    fun validateStep1(): Boolean {
        val s = _uiState.value
        val namaErr    = validateInput.validateNamaLengkap(s.namaLengkap)
        val emailErr   = validateInput.validateEmail(s.email)
        val usernameErr= validateInput.validateUsername(s.username)
        val passErr    = validateInput.validatePassword(s.password)
        val confirmErr = validateInput.validateConfirmPassword(s.password, s.confirmPassword)

        _uiState.update {
            it.copy(
                namaLengkapError      = namaErr,
                emailError            = emailErr,
                usernameError         = usernameErr,
                passwordError         = passErr,
                confirmPasswordError  = confirmErr
            )
        }
        return listOf(namaErr, emailErr, usernameErr, passErr, confirmErr).all { it == null }
    }

    /** Selesai & Mulai — validasi step 2 + daftar + simpan periode */
    fun register() {
        val s = _uiState.value

        val orgErr       = validateInput.validateNotEmpty(s.namaOrganisasi, "Nama organisasi")
        val mulaiErr     = validateInput.validateNotEmpty(s.periodeMulai, "Periode mulai")
        val selesaiErr   = validateInput.validateNotEmpty(s.periodeSelesai, "Periode selesai")

        if (orgErr != null || mulaiErr != null || selesaiErr != null) {
            _uiState.update {
                it.copy(
                    namaOrganisasiError = orgErr,
                    periodeMulaiError   = mulaiErr,
                    periodeSelesaiError = selesaiErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = registerUseCase(
                    email          = s.email,
                    password       = s.password,
                    namaLengkap    = s.namaLengkap,
                    username       = s.username,
                    namaOrganisasi = s.namaOrganisasi
                )
                // Simpan periode kas ke Room
                periodeDao.insertPeriode(
                    PeriodeEntity(
                        userId         = user.userId,
                        tanggalMulai   = s.periodeMulai,
                        tanggalSelesai = s.periodeSelesai
                    )
                )
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("email address is already") == true -> "Email sudah digunakan"
                    e.message?.contains("network") == true -> "Tidak ada koneksi internet"
                    else -> e.localizedMessage ?: "Pendaftaran gagal. Coba lagi."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun resetSuccessState() { _uiState.update { it.copy(isSuccess = false) } }
}