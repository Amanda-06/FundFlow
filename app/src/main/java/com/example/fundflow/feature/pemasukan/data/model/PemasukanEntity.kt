// ============================================================
// feature/pemasukan/data/model/PemasukanEntity.kt
// ============================================================
package com.example.fundflow.feature.pemasukan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pemasukan")
data class PemasukanEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pemasukan_id")
    val pemasukanId: Int = 0,

    @ColumnInfo(name = "deskripsi")
    val deskripsi: String,

    @ColumnInfo(name = "sumber")
    val sumber: String,                  // Iuran Anggota / Program Kerja / Sponsorship / Donasi / Lainnya

    @ColumnInfo(name = "metode")
    val metode: String,                  // Cash / Transfer

    @ColumnInfo(name = "qty")
    val qty: Int = 1,

    @ColumnInfo(name = "harga_satuan")
    val hargaSatuan: Double = 0.0,

    @ColumnInfo(name = "nominal")
    val nominal: Double,                 // = qty × harga_satuan

    @ColumnInfo(name = "tanggal")
    val tanggal: String,                 // "yyyy-MM-dd"

    @ColumnInfo(name = "catatan")
    val catatan: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)