// ============================================================
// feature/home/data/repository/HomeRepositoryImpl.kt
// ============================================================
package com.example.fundflow.feature.home.data.repository

import com.example.fundflow.core.util.Resource
import com.example.fundflow.feature.home.data.mapper.toDomainList
import com.example.fundflow.feature.home.data.remote.HolidayRemoteDataSource
import com.example.fundflow.feature.home.domain.model.DashboardSummary
import com.example.fundflow.feature.home.domain.model.Holiday
import com.example.fundflow.feature.home.domain.model.RecentTransaction
import com.example.fundflow.feature.home.domain.repository.HomeRepository
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.core.util.DateFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val pemasukanDao: PemasukanDao,
    private val pengeluaranDao: PengeluaranDao,
    private val iuranDao: IuranDao,
    private val remoteDataSource: HolidayRemoteDataSource
) : HomeRepository {

    override fun getDashboardSummary(): Flow<DashboardSummary> {
        val cal          = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH) + 1
        val currentYear  = cal.get(Calendar.YEAR)

        return combine(
            pemasukanDao.getTotalPemasukan(),
            pengeluaranDao.getTotalPengeluaran(),
            iuranDao.getIuranSummaryByMonth(currentMonth, currentYear),
            pemasukanDao.getRecentPemasukan(limit = 5),
            pengeluaranDao.getRecentPengeluaran(limit = 5)
        ) { totalMasuk, totalKeluar, iuranSummaryRaw, recentMasuk, recentKeluar ->

            val totalPemasukan   = totalMasuk ?: 0.0
            val totalPengeluaran = totalKeluar ?: 0.0
            val iuranTerkumpul   = iuranSummaryRaw?.totalTerkumpul ?: 0.0
            val saldo            = totalPemasukan + iuranTerkumpul - totalPengeluaran

            val iuranSummary = IuranSummary(
                lunasCount       = iuranSummaryRaw?.lunasCount ?: 0,
                belumBayarCount  = iuranSummaryRaw?.belumBayarCount ?: 0,
                totalTerkumpul   = iuranTerkumpul
            )

            // Gabungkan pemasukan + pengeluaran → sort by tanggal DESC → ambil 5 teratas
            val allRecent = (
                    recentMasuk.map {
                        RecentTransaction(
                            id        = it.pemasukanId,
                            title     = it.deskripsi,
                            subtitle  = it.sumber,
                            amount    = it.nominal,
                            isIncome  = true,
                            date      = it.tanggal
                        )
                    } + recentKeluar.map {
                        RecentTransaction(
                            id        = it.pengeluaranId,
                            title     = it.deskripsi,
                            subtitle  = it.kategori,
                            amount    = it.totalNominal,
                            isIncome  = false,
                            date      = it.tanggal
                        )
                    }
                    ).sortedByDescending { it.date }.take(5)

            DashboardSummary(
                totalSaldo         = saldo,
                totalPemasukan     = totalPemasukan,
                totalPengeluaran   = totalPengeluaran,
                iuranSummary       = iuranSummary,
                recentTransactions = allRecent
            )
        }
    }

    override suspend fun getPublicHolidays(year: Int): Resource<List<Holiday>> {
        return when (val result = remoteDataSource.getPublicHolidays(year)) {
            is Resource.Success -> Resource.Success(result.data.toDomainList())
            is Resource.Error   -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading
        }
    }
}
