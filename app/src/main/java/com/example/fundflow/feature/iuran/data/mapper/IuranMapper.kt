package com.example.fundflow.feature.iuran.data.mapper

import com.example.fundflow.feature.iuran.data.model.IuranAnggotaRow
import com.example.fundflow.feature.iuran.data.model.IuranSummaryRaw
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.model.IuranSummary

/**
 * Konversi baris hasil JOIN (anggota + iuran) menjadi domain model Iuran.
 * Jika anggota belum punya record iuran (kolom null), default ke belum bayar.
 */
fun IuranAnggotaRow.toDomain(bulan: Int, tahun: Int): Iuran = Iuran(
    iuranId          = iuranId ?: 0,
    anggotaId        = anggotaId,
    namaAnggota      = namaAnggota,
    bulan            = bulan,
    tahun            = tahun,
    nominal          = nominal ?: 0.0,
    statusBayar      = statusBayar ?: false,
    terlambat        = terlambat ?: false,
    metodePembayaran = metodePembayaran ?: "",
    tanggalBayar     = tanggalBayar,
    catatan          = catatan ?: ""
)

fun IuranSummaryRaw.toDomain(): IuranSummary = IuranSummary(
    lunasCount      = lunasCount,
    belumBayarCount = belumBayarCount,
    totalTerkumpul  = totalTerkumpul
)