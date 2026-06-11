package com.example.fundflow.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.feature.settings.domain.model.PeriodeKas
import com.example.fundflow.feature.settings.domain.usecase.ObservePeriodeUseCase
import com.example.fundflow.feature.settings.domain.usecase.UpdatePeriodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengaturanPeriodeViewModel @Inject constructor(
    private val observePeriode: ObservePeriodeUseCase,
    private val updatePeriode: UpdatePeriodeUseCase,
    private val authService: FirebaseAuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PengaturanPeriodeState())
    val uiState: StateFlow<PengaturanPeriodeState> = _uiState.asStateFlow()

    init {
        val userId = authService.currentUser?.uid.orEmpty()
        _uiState.update { it.copy(userId = userId) }

        if (userId.isNotEmpty()) {
            observePeriode(userId)
                .onEach { periode ->
                    if (periode != null) {
                        _uiState.update {
                            it.copy(
                                bulanMulai   = periode.bulanMulai,
                                bulanSelesai = periode.bulanSelesai,
                                isLoading    = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                .launchIn(viewModelScope)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onBulanMulaiChange(v: String)   = _uiState.update { it.copy(bulanMulai = v, bulanMulaiError = null) }
    fun onBulanSelesaiChange(v: String) = _uiState.update { it.copy(bulanSelesai = v, bulanSelesaiError = null) }

    fun onSave() {
        val s = _uiState.value
        val mulaiErr   = if (s.bulanMulai.isBlank()) "Periode mulai tidak boleh kosong" else null
        val selesaiErr = when {
            s.bulanSelesai.isBlank()       -> "Periode selesai tidak boleh kosong"
            s.bulanMulai > s.bulanSelesai  -> "Periode selesai harus setelah periode mulai"
            else                           -> null
        }

        if (mulaiErr != null || selesaiErr != null) {
            _uiState.update { it.copy(bulanMulaiError = mulaiErr, bulanSelesaiError = selesaiErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                updatePeriode(
                    PeriodeKas(
                        userId       = s.userId,
                        bulanMulai   = s.bulanMulai,
                        bulanSelesai = s.bulanSelesai
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


// ============================================================
// feature/settings/presentation/PengaturanPeriodeScreen.kt
// ============================================================
