// feature/laporan/data/repository/LaporanRepositoryImpl.kt
package com.example.fundflow.feature.laporan.data.repository

import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.anggota.data.mapper.toDomain
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.laporan.domain.model.*
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.core.util.DateFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class LaporanRepositoryImpl @Inject constructor(
    private val iuranDao: IuranDao,
    private val anggotaDao: AnggotaDao,
    private val pemasukanDao: PemasukanDao,
    private val pengeluaranDao: PengeluaranDao
) : LaporanRepository {

    override suspend fun generateLaporanIuranBulanan(
        startDate: String,
        endDate: String
    ): LaporanIuranBulanan {
        val start = LocalDate.parse("$startDate-01")
        val end   = LocalDate.parse("$endDate-01")

        val rincian = mutableListOf<RincianIuranBulan>()
        var current = start

        while (!current.isAfter(end)) {
            val bulan = current.monthValue
            val tahun = current.year
            val total = iuranDao.getTotalIuranByMonth(bulan, tahun) ?: 0.0

            // FIX: Memasukkan angka mentah 'bulan' (Int) dan 'tahun' (Int) sesuai struktur model baru
            rincian.add(
                RincianIuranBulan(
                    bulan  = bulan,
                    tahun  = tahun,
                    jumlah = total
                )
            )
            current = current.plusMonths(1)
        }

        return LaporanIuranBulanan(
            rincianBulan      = rincian,
            totalKeseluruhan  = rincian.sumOf { it.jumlah }
        )
    }

    override suspend fun generateLaporanStatusBayar(
        bulan: Int,
        tahun: Int
    ): LaporanStatusBayar {
        // Ambil snapshot data list saat ini dengan .first() lalu petakan ke objek Domain (Anggota) lewat .toDomain()
        val allAnggota = anggotaDao.getAllAnggota().first().map { it.toDomain() }

        val iuranList      = iuranDao.getIuranByMonth(bulan, tahun)
        val iuranMap       = iuranList.associateBy { it.anggotaId }

        val rincian = allAnggota.map { anggota ->
            val iuran = iuranMap[anggota.anggotaId]
            StatusBayarAnggota(
                namaAnggota  = anggota.namaAnggota,
                statusBayar  = iuran?.statusBayar ?: false,
                nominal      = iuran?.nominal ?: 0.0,
                tanggalBayar = iuran?.tanggalBayar
            )
        }

        // FIX: Menggunakan Locale.getDefault() agar menyesuaikan dengan bahasa perangkat
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        val bulanLabel = LocalDate.of(tahun, bulan, 1).format(formatter).replaceFirstChar { it.uppercase() }

        return LaporanStatusBayar(
            bulan           = bulanLabel,
            tahun           = tahun,
            rincianAnggota  = rincian,
            totalLunas      = rincian.count { it.statusBayar },
            totalBelumBayar = rincian.count { !it.statusBayar },
            totalTerkumpul  = rincian.filter { it.statusBayar }.sumOf { it.nominal }
        )
    }

    override suspend fun generateLaporanDetailKeuangan(
        startDate: String,
        endDate: String
    ): LaporanDetailKeuangan {
        val pemasukan   = pemasukanDao.getByDateRange(startDate, endDate)
        val pengeluaran = pengeluaranDao.getByDateRange(startDate, endDate)

        val itemPemasukan = pemasukan.map {
            ItemDetailKeuangan(
                tanggal    = it.tanggal,
                deskripsi  = it.deskripsi,
                keterangan = it.sumber,
                nominal    = it.nominal,
                isIncome   = true
            )
        }
        val itemPengeluaran = pengeluaran.map {
            ItemDetailKeuangan(
                tanggal    = it.tanggal,
                deskripsi  = it.deskripsi,
                keterangan = it.kategori,
                nominal    = it.totalNominal,
                isIncome   = false
            )
        }

        val totalMasuk  = pemasukan.sumOf { it.nominal }
        val totalKeluar = pengeluaran.sumOf { it.totalNominal }

        return LaporanDetailKeuangan(
            periode          = "$startDate s/d $endDate",
            daftarPemasukan  = itemPemasukan,
            daftarPengeluaran = itemPengeluaran,
            totalPemasukan   = totalMasuk,
            totalPengeluaran = totalKeluar,
            saldoAkhir       = totalMasuk - totalKeluar
        )
    }
}