// ============================================================
// feature/laporan/data/model/LaporanEntity.kt
// ============================================================
package com.example.fundflow.feature.laporan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laporan")
data class LaporanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "laporan_id")
    val laporanId: Int = 0,

    @ColumnInfo(name = "jenis_laporan")
    val jenisLaporan: String,          // "iuran_bulanan" | "status_bayar" | "detail_keuangan"

    @ColumnInfo(name = "periode")
    val periode: String,               // misal: "Maret 2026 - Desember 2026"

    @ColumnInfo(name = "total_pemasukan")
    val totalPemasukan: Double = 0.0,

    @ColumnInfo(name = "total_pengeluaran")
    val totalPengeluaran: Double = 0.0,

    @ColumnInfo(name = "saldo_akhir")
    val saldoAkhir: Double = 0.0,

    @ColumnInfo(name = "tanggal_generate")
    val tanggalGenerate: Long = System.currentTimeMillis()
)