package com.example.fundflow.feature.iuran.domain.model

data class Iuran(
    val iuranId: Int            = 0,
    val anggotaId: Int,
    val namaAnggota: String,
    val bulan: Int,                  // 1-12
    val tahun: Int,
    val nominal: Double         = 0.0,
    val statusBayar: Boolean    = false,
    val terlambat: Boolean      = false,
    val metodePembayaran: String = "",
    val tanggalBayar: String?    = null,   // "yyyy-MM-dd"
    val catatan: String          = ""
) {
    companion object {
        val METODE_OPTIONS = listOf("Cash", "Transfer")
    }
}


// ============================================================
// feature/iuran/domain/model/IuranSummary.kt
// ============================================================



// ============================================================
// feature/iuran/domain/repository/IuranRepository.kt
// ============================================================



// ============================================================
// feature/iuran/domain/usecase/GetIuranListUseCase.kt
// ============================================================



// ============================================================
// feature/iuran/domain/usecase/GetIuranSummaryUseCase.kt
// ============================================================



// ============================================================
// feature/iuran/domain/usecase/SaveIuranUseCase.kt
// ============================================================



// ============================================================
// feature/iuran/domain/usecase/GetPeriodeMonthsUseCase.kt
// ============================================================
