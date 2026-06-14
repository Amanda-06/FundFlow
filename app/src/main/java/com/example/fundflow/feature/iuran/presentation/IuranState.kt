package com.example.fundflow.feature.iuran.presentation

import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import com.example.fundflow.feature.iuran.domain.usecase.MonthOption

enum class IuranFilter { SEMUA, LUNAS, BELUM_BAYAR }

data class IuranState(
    // Periode bulan
    val availableMonths: List<MonthOption> = emptyList(),
    val selectedMonth: MonthOption?         = null,
    val showMonthPicker: Boolean            = false,

    // Data iuran
    val iuranList: List<Iuran>              = emptyList(),
    val filteredList: List<Iuran>           = emptyList(),
    val summary: IuranSummary               = IuranSummary(),

    // Search & filter
    val searchQuery: String                 = "",
    val activeFilter: IuranFilter           = IuranFilter.SEMUA,

    val isLoading: Boolean                  = true,

    // Bottom sheet edit detail iuran anggota
    val showDetailSheet: Boolean            = false,
    val selectedIuran: Iuran?                = null,

    // Form fields di bottom sheet
    val formStatusBayar: Boolean             = false,
    val formTerlambat: Boolean               = false,
    val formNominal: String                  = "",
    val formMetode: String                   = "",
    val formTanggalBayar: String             = "",
    val formCatatan: String                  = "",

    val errorMessage: String?                = null,
    val successMessage: String?              = null
)