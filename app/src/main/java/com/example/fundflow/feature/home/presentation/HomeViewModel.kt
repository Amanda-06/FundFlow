// ============================================================
// feature/home/presentation/HomeViewModel.kt
// ============================================================
package com.example.fundflow.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.util.Resource
import com.example.fundflow.feature.auth.domain.usecase.LogoutUseCase
import com.example.fundflow.feature.home.domain.usecase.GetDashboardSummaryUseCase
import com.example.fundflow.feature.home.domain.usecase.GetPublicHolidaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardSummary: GetDashboardSummaryUseCase,
    private val getPublicHolidays: GetPublicHolidaysUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        observeDashboard()
        loadHolidays()
    }

    private fun observeDashboard() {
        getDashboardSummary()
            .onEach { summary ->
                _uiState.update { it.copy(summary = summary, isLoadingSummary = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoadingSummary = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadHolidays() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHolidays = true) }
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            when (val result = getPublicHolidays(year)) {
                is Resource.Success -> {
                    val today   = LocalDate.now().toString()   // "2026-06-10"
                    val upcoming = result.data
                        .filter { it.date >= today }
                        .minByOrNull { it.date }
                    _uiState.update {
                        it.copy(
                            holidays          = result.data,
                            upcomingHoliday   = upcoming,
                            isLoadingHolidays = false,
                            holidayError      = null
                        )
                    }
                }
                is Resource.Error -> {
                    android.util.Log.e("HOLIDAY_ERROR", "Penyebab Gagal: ${result.message}")

                    _uiState.update {
                        it.copy(isLoadingHolidays = false, holidayError = result.message)
                    }
                }
                else -> Unit
            }
        }
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onDone()
        }
    }
}
