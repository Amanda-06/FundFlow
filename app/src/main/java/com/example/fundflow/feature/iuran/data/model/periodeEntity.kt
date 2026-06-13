package com.example.fundflow.feature.iuran.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "periode")
data class PeriodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "periode_id")
    val periodeId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "tanggal_mulai")
    val tanggalMulai: String,      // format "yyyy-MM", contoh: "2026-03"

    @ColumnInfo(name = "tanggal_selesai")
    val tanggalSelesai: String,    // format "yyyy-MM"

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)