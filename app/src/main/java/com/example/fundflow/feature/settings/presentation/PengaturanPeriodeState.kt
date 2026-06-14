package com.example.fundflow.feature.settings.presentation

data class PengaturanPeriodeState(
    val userId: String           = "",
    val bulanMulai: String        = "",
    val bulanSelesai: String      = "",

    val bulanMulaiError: String?  = null,
    val bulanSelesaiError: String? = null,

    val isLoading: Boolean        = true,
    val isSaving: Boolean         = false,
    val isSuccess: Boolean        = false,
    val errorMessage: String?     = null
)