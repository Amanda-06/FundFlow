package com.example.fundflow.feature.iuran.domain.model

data class Periode(
    val periodeId: Int = 0,
    val userId: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val createdAt: Long = System.currentTimeMillis()
)