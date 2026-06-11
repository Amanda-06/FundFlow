package com.example.fundflow.feature.laporan.data.repository

import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.anggota.data.mapper.toDomain // BARU: Import mapper untuk konversi Entity ke Domain
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.laporan.domain.model.*
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.core.util.DateFormatter
import kotlinx.coroutines.flow.first // BARU: Import untuk mengambil emisi pertama dari Flow
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

    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))

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
            rincian.add(
                RincianIuranBulan(
                    bulan  = current.format(monthFormatter),
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
        // PERBAIKAN: Ambil snapshot data list saat ini dengan .first() lalu petakan ke objek Domain (Anggota) lewat .toDomain()
        val allAnggota = anggotaDao.getAllAnggota().first().map { it.toDomain() }

        val iuranList      = iuranDao.getIuranByMonth(bulan, tahun)
        val iuranMap       = iuranList.associateBy { it.anggotaId }

        // Sekarang .map di bawah ini murni milik Kotlin Collections List biasa, bukan milik Flow lagi
        val rincian = allAnggota.map { anggota ->
            val iuran = iuranMap[anggota.anggotaId]
            StatusBayarAnggota(
                namaAnggota  = anggota.namaAnggota, // Sekarang variabel aman dan terdefinisi dengan benar
                statusBayar  = iuran?.statusBayar ?: false,
                nominal      = iuran?.nominal ?: 0.0,
                tanggalBayar = iuran?.tanggalBayar
            )
        }

        // Membersihkan boilerplate Calendar yang tidak terpakai dan mengoptimalkan label bulan dengan LocalDate
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
        val bulanLabel = LocalDate.of(tahun, bulan, 1).format(formatter)

        return LaporanStatusBayar(
            bulan           = bulanLabel,
            tahun           = tahun,
            rincianAnggota  = rincian, // Casting manual "as List<...>" dihapus karena otomatis sudah berupa List murni
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