package com.example.fundflow.feature.iuran.domain.usecase

import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIuranSummaryUseCase @Inject constructor(
    private val repository: IuranRepository
) {
    operator fun invoke(bulan: Int, tahun: Int): Flow<IuranSummary> =
        repository.observeSummary(bulan, tahun)
}