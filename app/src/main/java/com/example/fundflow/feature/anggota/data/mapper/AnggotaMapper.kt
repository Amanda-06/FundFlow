package com.example.fundflow.feature.anggota.data.mapper

import com.example.fundflow.feature.anggota.data.model.AnggotaEntity
import com.example.fundflow.feature.anggota.domain.model.Anggota

fun AnggotaEntity.toDomain(): Anggota = Anggota(
    anggotaId   = anggotaId,
    namaAnggota = namaAnggota,
    createdAt   = createdAt
)

fun Anggota.toEntity(): AnggotaEntity = AnggotaEntity(
    anggotaId   = anggotaId,
    namaAnggota = namaAnggota,
    createdAt   = createdAt
)

