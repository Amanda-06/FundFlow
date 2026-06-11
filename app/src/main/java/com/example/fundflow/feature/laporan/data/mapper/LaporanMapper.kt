package com.example.fundflow.feature.laporan.data.mapper

import com.example.fundflow.feature.laporan.data.model.LaporanEntity
import com.example.fundflow.feature.laporan.domain.model.Laporan

fun LaporanEntity.toDomain(): Laporan = Laporan(
    laporanId        = laporanId,
    jenisLaporan     = jenisLaporan,
    periode          = periode,
    totalPemasukan   = totalPemasukan,
    totalPengeluaran = totalPengeluaran,
    saldoAkhir       = saldoAkhir,
    tanggalGenerate  = tanggalGenerate
)

fun Laporan.toEntity(): LaporanEntity = LaporanEntity(
    laporanId        = laporanId,
    jenisLaporan     = jenisLaporan,
    periode          = periode,
    totalPemasukan   = totalPemasukan,
    totalPengeluaran = totalPengeluaran,
    saldoAkhir       = saldoAkhir,
    tanggalGenerate  = tanggalGenerate
)