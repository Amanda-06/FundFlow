// ============================================================
// feature/anggota/domain/model/Anggota.kt
// ============================================================
package com.example.fundflow.feature.anggota.domain.model

data class Anggota(
    val anggotaId: Int    = 0,
    val namaAnggota: String,
    val createdAt: Long   = System.currentTimeMillis()
)
