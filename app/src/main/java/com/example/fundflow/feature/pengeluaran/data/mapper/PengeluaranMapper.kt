package com.example.fundflow.feature.pengeluaran.data.mapper

import com.example.fundflow.feature.pengeluaran.data.model.PengeluaranEntity
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran

fun PengeluaranEntity.toDomain(): Pengeluaran = Pengeluaran(
    pengeluaranId = pengeluaranId,
    deskripsi     = deskripsi,
    kategori      = kategori,
    namaProgram   = namaProgram,
    metode        = metode,
    quantity      = quantity,
    hargaSatuan   = hargaSatuan,
    totalNominal  = totalNominal,
    tanggal       = tanggal,
    catatan       = catatan,
    createdAt     = createdAt
)

fun Pengeluaran.toEntity(): PengeluaranEntity = PengeluaranEntity(
    pengeluaranId = pengeluaranId,
    deskripsi     = deskripsi,
    kategori      = kategori,
    namaProgram   = namaProgram,
    metode        = metode,
    quantity      = quantity,
    hargaSatuan   = hargaSatuan,
    totalNominal  = totalNominal,
    tanggal       = tanggal,
    catatan       = catatan,
    createdAt     = createdAt
)

fun List<PengeluaranEntity>.toDomainList() = map { it.toDomain() }