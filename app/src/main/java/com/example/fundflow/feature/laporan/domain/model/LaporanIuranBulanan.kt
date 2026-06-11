package com.example.fundflow.feature.laporan.domain.model

data class LaporanIuranBulanan(
    val rincianBulan: List<RincianIuranBulan>,
    val totalKeseluruhan: Double
)

data class RincianIuranBulan(
    val bulan: String,   // "Maret 2026"
    val jumlah: Double
)