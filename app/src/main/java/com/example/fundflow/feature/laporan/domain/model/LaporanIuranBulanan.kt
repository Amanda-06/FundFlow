package com.example.fundflow.feature.laporan.domain.model

data class LaporanIuranBulanan(
    val rincianBulan: List<RincianIuranBulan>,
    val totalKeseluruhan: Double
)

data class RincianIuranBulan(
    val bulan: Int,   // ganti jadi Int (contoh: 3 untuk Maret)
    val tahun: Int,   // tambahkan field tahun agar lengkap
    val jumlah: Double
)