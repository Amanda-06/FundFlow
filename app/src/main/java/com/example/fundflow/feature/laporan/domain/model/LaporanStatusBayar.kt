package com.example.fundflow.feature.laporan.domain.model

data class LaporanStatusBayar(
    val bulan: String,
    val tahun: Int,
    val rincianAnggota: List<StatusBayarAnggota>,
    val totalLunas: Int,
    val totalBelumBayar: Int,
    val totalTerkumpul: Double
)

data class StatusBayarAnggota(
    val namaAnggota: String,
    val statusBayar: Boolean,  // true = lunas
    val nominal: Double,
    val tanggalBayar: String?
)