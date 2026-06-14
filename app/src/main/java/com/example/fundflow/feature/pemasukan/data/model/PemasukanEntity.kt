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
    val sumber: String,

    @ColumnInfo(name = "metode")
    val metode: String,

    @ColumnInfo(name = "nominal")
    val nominal: Double,

    @ColumnInfo(name = "tanggal")
    val tanggal: String,

    @ColumnInfo(name = "catatan")
    val catatan: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)