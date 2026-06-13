package com.example.fundflow.feature.iuran.data.mapper

import com.example.fundflow.feature.iuran.data.model.PeriodeEntity
import com.example.fundflow.feature.iuran.domain.model.Periode

fun PeriodeEntity.toDomain(): Periode = Periode(
    periodeId = this.periodeId,
    userId = this.userId,
    tanggalMulai = this.tanggalMulai,
    tanggalSelesai = this.tanggalSelesai,
    createdAt = this.createdAt
)

fun Periode.toEntity(): PeriodeEntity = PeriodeEntity(
    periodeId = this.periodeId,
    userId = this.userId,
    tanggalMulai = this.tanggalMulai,
    tanggalSelesai = this.tanggalSelesai,
    createdAt = this.createdAt
)