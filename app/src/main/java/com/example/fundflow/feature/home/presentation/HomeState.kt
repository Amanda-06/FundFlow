// ============================================================
// feature/home/presentation/HomeState.kt
// ============================================================
package com.example.fundflow.feature.home.presentation

import com.example.fundflow.feature.home.domain.model.DashboardSummary
import com.example.fundflow.feature.home.domain.model.Holiday

data class HomeState(
    val summary: DashboardSummary      = DashboardSummary(),
    val holidays: List<Holiday>        = emptyList(),
    val upcomingHoliday: Holiday?      = null,
    val isLoadingSummary: Boolean      = true,
    val isLoadingHolidays: Boolean     = false,
    val holidayError: String?          = null,
    val userName: String               = ""
)

