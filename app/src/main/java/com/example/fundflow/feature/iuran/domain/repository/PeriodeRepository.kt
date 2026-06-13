package com.example.fundflow.feature.iuran.domain.repository

import com.example.fundflow.feature.iuran.domain.model.Periode
import kotlinx.coroutines.flow.Flow

interface PeriodeRepository {
    suspend fun savePeriode(periode: Periode): Long
    suspend fun getPeriodeByUserId(userId: String): Periode?
    fun observePeriode(userId: String): Flow<Periode?>
    suspend fun deleteByUserId(userId: String)
    suspend fun syncWithCloud() // Untuk sinkronisasi otomatis antar-perangkat
}