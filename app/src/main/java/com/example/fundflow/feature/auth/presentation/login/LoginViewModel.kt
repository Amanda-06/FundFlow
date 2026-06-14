package com.example.fundflow.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.auth.domain.usecase.LoginUseCase
import com.example.fundflow.feature.auth.domain.usecase.ValidateInputUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateInput: ValidateInputUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    // ── Input handlers ────────────────────────────────────────
    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    // ── Aksi login ────────────────────────────────────────────
    fun login() {
        val state = _uiState.value

        // Validasi
        val emailErr    = validateInput.validateEmail(state.email)
        val passwordErr = validateInput.validatePassword(state.password)

        if (emailErr != null || passwordErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                loginUseCase(state.email, state.password)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                val message = when {
                    e.message?.contains("no user record") == true  -> "Akun tidak ditemukan"
                    e.message?.contains("password is invalid") == true -> "Kata sandi salah"
                    e.message?.contains("network") == true         -> "Tidak ada koneksi internet"
                    else -> e.localizedMessage ?: "Login gagal. Coba lagi."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}