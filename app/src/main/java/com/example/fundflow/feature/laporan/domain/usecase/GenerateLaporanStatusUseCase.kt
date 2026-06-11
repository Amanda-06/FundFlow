package com.example.fundflow.feature.laporan.domain.usecase

import com.example.fundflow.feature.laporan.domain.model.*
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import javax.inject.Inject

class GenerateLaporanStatusUseCase @Inject constructor(
    private val repository: LaporanRepository
) {
    suspend operator fun invoke(bulan: Int, tahun: Int): LaporanStatusBayar =
        repository.generateLaporanStatusBayar(bulan, tahun)
}