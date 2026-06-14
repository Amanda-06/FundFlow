package com.example.fundflow.feature.laporan.domain.model

data class LaporanIuranBulanan(
    val rincianBulan: List<RincianIuranBulan>,
    val totalKeseluruhan: Double
)

data class RincianIuranBulan(
    val bulan: Int,
    val tahun: Int,
    val jumlah: Double
)