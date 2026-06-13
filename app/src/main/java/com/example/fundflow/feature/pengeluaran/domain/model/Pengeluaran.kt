package com.example.fundflow.feature.pengeluaran.domain.model

data class Pengeluaran(
    val pengeluaranId: Int  = 0,
    val deskripsi: String,
    val kategori: String,
    val namaProgram: String = "",
    val metode: String,
    val quantity: Int       = 1,
    val hargaSatuan: Double = 0.0,
    val totalNominal: Double,
    val tanggal: String,
    val catatan: String     = "",
    val createdAt: Long     = System.currentTimeMillis()
)