package com.example.fundflow.feature.iuran.domain.repository

import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import kotlinx.coroutines.flow.Flow

interface IuranRepository {
    fun observeIuranByMonth(bulan: Int, tahun: Int): Flow<List<Iuran>>
    fun observeSummary(bulan: Int, tahun: Int): Flow<IuranSummary>
    suspend fun saveIuran(iuran: Iuran)
}