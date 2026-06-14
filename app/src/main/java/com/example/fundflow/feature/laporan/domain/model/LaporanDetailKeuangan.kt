package com.example.fundflow.feature.laporan.domain.model

data class LaporanDetailKeuangan(
    val periode: String,
    val daftarPemasukan: List<ItemDetailKeuangan>,
    val daftarPengeluaran: List<ItemDetailKeuangan>,
    val totalPemasukan: Double,
    val totalPengeluaran: Double,
    val saldoAkhir: Double
)

data class ItemDetailKeuangan(
    val tanggal: String,
    val deskripsi: String,
    val keterangan: String,
    val nominal: Double,
    val isIncome: Boolean
)