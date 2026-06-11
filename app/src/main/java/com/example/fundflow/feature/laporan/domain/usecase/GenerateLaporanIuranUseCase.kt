package com.example.fundflow.feature.laporan.domain.usecase

import com.example.fundflow.feature.laporan.domain.model.*
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import javax.inject.Inject

class GenerateLaporanIuranUseCase @Inject constructor(
    private val repository: LaporanRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String): LaporanIuranBulanan =
        repository.generateLaporanIuranBulanan(startDate, endDate)
}