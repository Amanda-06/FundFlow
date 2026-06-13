package com.example.fundflow.feature.pemasukan.domain.model

data class Pemasukan(
    val pemasukanId: Int    = 0,
    val deskripsi: String,
    val sumber: String,
    val metode: String,
    val nominal: Double,
    val tanggal: String,
    val catatan: String     = "",
    val createdAt: Long     = System.currentTimeMillis()
)