
// ============================================================
// feature/home/domain/model/DashboardSummary.kt
// ============================================================
package com.example.fundflow.feature.home.domain.model

import com.example.fundflow.feature.iuran.domain.model.IuranSummary

data class DashboardSummary(
    val totalSaldo: Double         = 0.0,
    val totalPemasukan: Double     = 0.0,
    val totalPengeluaran: Double   = 0.0,
    val iuranSummary: IuranSummary = IuranSummary(),
    val recentTransactions: List<RecentTransaction> = emptyList()
)

data class RecentTransaction(
    val id: Int,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isIncome: Boolean,   // true = pemasukan / iuran, false = pengeluaran
    val date: String
)
