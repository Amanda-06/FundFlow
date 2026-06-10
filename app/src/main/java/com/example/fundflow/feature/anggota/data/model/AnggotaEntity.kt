// ============================================================
// feature/anggota/data/model/AnggotaEntity.kt
// ============================================================
package com.example.fundflow.feature.anggota.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anggota")
data class AnggotaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "anggota_id")
    val anggotaId: Int = 0,

    @ColumnInfo(name = "nama_anggota")
    val namaAnggota: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
