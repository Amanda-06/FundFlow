package com.example.fundflow.feature.pengeluaran.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pengeluaran")
data class PengeluaranEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pengeluaran_id")
    val pengeluaranId: Int = 0,

    @ColumnInfo(name = "deskripsi")
    val deskripsi: String,

    @ColumnInfo(name = "kategori")
    val kategori: String,               // Program Kerja / Inventaris / Operasional / Konsumsi / Lainnya

    @ColumnInfo(name = "nama_program")
    val namaProgram: String = "",       // Opsional — nama kegiatan/proker

    @ColumnInfo(name = "metode")
    val metode: String,                 // Cash / Transfer

    @ColumnInfo(name = "quantity")
    val quantity: Int = 1,

    @ColumnInfo(name = "harga_satuan")
    val hargaSatuan: Double = 0.0,

    @ColumnInfo(name = "total_nominal")
    val totalNominal: Double,           // = quantity × harga_satuan

    @ColumnInfo(name = "tanggal")
    val tanggal: String,                // "yyyy-MM-dd"

    @ColumnInfo(name = "catatan")
    val catatan: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)