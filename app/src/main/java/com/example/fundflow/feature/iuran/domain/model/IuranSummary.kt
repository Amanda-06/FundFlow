package com.example.fundflow.feature.iuran.domain.model

data class IuranSummary(
    val lunasCount: Int          = 0,
    val belumBayarCount: Int     = 0,
    val totalTerkumpul: Double   = 0.0
) {
    val totalAnggota: Int get() = lunasCount + belumBayarCount
}