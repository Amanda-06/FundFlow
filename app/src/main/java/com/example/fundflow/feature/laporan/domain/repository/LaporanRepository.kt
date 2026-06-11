package com.example.fundflow.feature.laporan.domain.repository

import com.example.fundflow.feature.laporan.domain.model.*
import kotlinx.coroutines.flow.Flow

interface LaporanRepository {
    suspend fun generateLaporanIuranBulanan(startDate: String, endDate: String): LaporanIuranBulanan
    suspend fun generateLaporanStatusBayar(bulan: Int, tahun: Int): LaporanStatusBayar
    suspend fun generateLaporanDetailKeuangan(startDate: String, endDate: String): LaporanDetailKeuangan
}