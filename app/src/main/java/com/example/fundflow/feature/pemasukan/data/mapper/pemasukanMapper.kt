package com.example.fundflow.feature.pemasukan.data.mapper

import com.example.fundflow.feature.pemasukan.data.model.PemasukanEntity
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan

fun PemasukanEntity.toDomain(): Pemasukan = Pemasukan(
    pemasukanId = pemasukanId,
    deskripsi   = deskripsi,
    sumber      = sumber,
    metode      = metode,
    nominal     = nominal,
    tanggal     = tanggal,
    catatan     = catatan,
    createdAt   = createdAt
)

fun Pemasukan.toEntity(): PemasukanEntity = PemasukanEntity(
    pemasukanId = pemasukanId,
    deskripsi   = deskripsi,
    sumber      = sumber,
    metode      = metode,
    nominal     = nominal,
    tanggal     = tanggal,
    catatan     = catatan,
    createdAt   = createdAt
)

fun List<PemasukanEntity>.toDomainList() = map { it.toDomain() }