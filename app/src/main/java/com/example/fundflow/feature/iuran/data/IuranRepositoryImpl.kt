package com.example.fundflow.feature.iuran.data.repository

import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.data.mapper.toDomain
import com.example.fundflow.feature.iuran.data.model.IuranEntity
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IuranRepositoryImpl @Inject constructor(
    private val dao: IuranDao
) : IuranRepository {

    override fun observeIuranByMonth(bulan: Int, tahun: Int): Flow<List<Iuran>> =
        dao.observeIuranByMonth(bulan, tahun).map { rows ->
            rows.map { it.toDomain(bulan, tahun) }
        }

    override fun observeSummary(bulan: Int, tahun: Int): Flow<IuranSummary> =
        dao.getIuranSummaryByMonth(bulan, tahun).map { raw ->
            raw?.toDomain() ?: IuranSummary()
        }

    override suspend fun saveIuran(iuran: Iuran) {
        // Cari record existing untuk dapatkan iuran_id (jika ada) agar REPLACE tepat sasaran
        val existing = dao.getIuranByAnggotaAndMonth(iuran.anggotaId, iuran.bulan, iuran.tahun)
        dao.upsertIuran(
            IuranEntity(
                iuranId          = existing?.iuranId ?: 0,
                anggotaId        = iuran.anggotaId,
                bulan            = iuran.bulan,
                tahun            = iuran.tahun,
                nominal          = iuran.nominal,
                statusBayar      = iuran.statusBayar,
                terlambat        = iuran.terlambat,
                metodePembayaran = iuran.metodePembayaran,
                tanggalBayar     = iuran.tanggalBayar,
                catatan          = iuran.catatan,
                createdAt        = existing?.createdAt ?: System.currentTimeMillis()
            )
        )
    }
}