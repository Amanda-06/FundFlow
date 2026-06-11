package com.example.fundflow.feature.iuran.data.model

import androidx.room.ColumnInfo

/**
 * Hasil LEFT JOIN antara tabel `anggota` dan `iuran` untuk bulan/tahun tertentu.
 * Jika anggota belum punya record iuran di bulan tsb, kolom-kolom iuran bernilai null.
 */
data class IuranAnggotaRow(
    @ColumnInfo(name = "anggota_id")        val anggotaId: Int,
    @ColumnInfo(name = "nama_anggota")      val namaAnggota: String,
    @ColumnInfo(name = "iuran_id")          val iuranId: Int?,
    @ColumnInfo(name = "nominal")           val nominal: Double?,
    @ColumnInfo(name = "status_bayar")      val statusBayar: Boolean?,
    @ColumnInfo(name = "terlambat")         val terlambat: Boolean?,
    @ColumnInfo(name = "metode_pembayaran") val metodePembayaran: String?,
    @ColumnInfo(name = "tanggal_bayar")     val tanggalBayar: String?,
    @ColumnInfo(name = "catatan")           val catatan: String?
)
