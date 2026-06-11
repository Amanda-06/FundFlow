package com.example.fundflow.feature.pemasukan.domain.model

data class Pemasukan(
    val pemasukanId: Int    = 0,
    val deskripsi: String,
    val sumber: String,
    val metode: String,
    val qty: Int            = 1,
    val hargaSatuan: Double = 0.0,
    val nominal: Double,
    val tanggal: String,
    val catatan: String     = "",
    val createdAt: Long     = System.currentTimeMillis()
) {
    companion object {
        // Pilihan sumber dana
        val SUMBER_OPTIONS = listOf(
            "Iuran Anggota",
            "Program Kerja",
            "Sponsorship",
            "Donasi",
            "Dana Usaha",
            "Lainnya"
        )
        // Pilihan metode pembayaran
        val METODE_OPTIONS = listOf("Cash", "Transfer")
    }
}