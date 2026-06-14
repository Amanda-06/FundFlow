package com.example.fundflow.feature.iuran.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fundflow.feature.anggota.data.model.AnggotaEntity

@Entity(
    tableName = "iuran",
    foreignKeys = [
        ForeignKey(
            entity        = AnggotaEntity::class,
            parentColumns = ["anggota_id"],
            childColumns  = ["anggota_id"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("anggota_id"),
        Index(value = ["anggota_id", "bulan", "tahun"], unique = true)
    ]
)
data class IuranEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "iuran_id")
    val iuranId: Int = 0,

    @ColumnInfo(name = "anggota_id")
    val anggotaId: Int,

    @ColumnInfo(name = "bulan")
    val bulan: Int,

    @ColumnInfo(name = "tahun")
    val tahun: Int,

    @ColumnInfo(name = "nominal")
    val nominal: Double = 0.0,

    @ColumnInfo(name = "status_bayar")
    val statusBayar: Boolean = false,

    @ColumnInfo(name = "terlambat")
    val terlambat: Boolean = false,

    @ColumnInfo(name = "metode_pembayaran")
    val metodePembayaran: String = "",

    @ColumnInfo(name = "tanggal_bayar")
    val tanggalBayar: String? = null,

    @ColumnInfo(name = "catatan")
    val catatan: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)