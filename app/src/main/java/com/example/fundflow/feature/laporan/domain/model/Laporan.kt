package com.example.fundflow.feature.laporan.domain.model

data class Laporan(
    val laporanId: Int       = 0,
    val jenisLaporan: String,
    val periode: String,
    val totalPemasukan: Double   = 0.0,
    val totalPengeluaran: Double = 0.0,
    val saldoAkhir: Double       = 0.0,
    val tanggalGenerate: Long    = System.currentTimeMillis()
)