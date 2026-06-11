package com.example.fundflow.feature.settings.domain.model

data class PeriodeKas(
    val periodeId: Int     = 0,
    val userId: String,
    val bulanMulai: String,    // format "yyyy-MM"
    val bulanSelesai: String   // format "yyyy-MM"
)