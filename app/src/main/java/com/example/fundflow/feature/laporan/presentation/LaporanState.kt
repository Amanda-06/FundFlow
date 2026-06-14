package com.example.fundflow.feature.laporan.presentation

import com.example.fundflow.feature.laporan.domain.model.LaporanDetailKeuangan
import com.example.fundflow.feature.laporan.domain.model.LaporanIuranBulanan
import com.example.fundflow.feature.laporan.domain.model.LaporanStatusBayar
import com.example.fundflow.feature.iuran.domain.usecase.MonthOption

enum class LaporanType { IURAN_BULANAN, STATUS_BAYAR, DETAIL_KEUANGAN }

data class LaporanState(
    val availableMonths: List<MonthOption> = emptyList(),

    // Bottom sheet aktif
    val activeSheet: LaporanType?          = null,
    val isGenerating: Boolean              = false,

    // Hasil generate
    val laporanIuran: LaporanIuranBulanan?       = null,
    val laporanStatus: LaporanStatusBayar?       = null,
    val laporanDetail: LaporanDetailKeuangan?    = null,

    // Untuk laporan status bayar — pilih bulan spesifik
    val selectedMonthForStatus: MonthOption?     = null,
    val showStatusMonthPicker: Boolean           = false,

    val isExporting: Boolean               = false,
    val errorMessage: String?              = null,
    val successMessage: String?            = null,
    val exportedFilePath: String?          = null
)