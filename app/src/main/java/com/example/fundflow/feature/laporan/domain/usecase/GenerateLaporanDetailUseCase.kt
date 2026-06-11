package com.example.fundflow.feature.laporan.domain.usecase

import com.example.fundflow.feature.laporan.domain.model.*
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import javax.inject.Inject

class GenerateLaporanDetailUseCase @Inject constructor(
    private val repository: LaporanRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String): LaporanDetailKeuangan =
        repository.generateLaporanDetailKeuangan(startDate, endDate)
}