package com.example.fundflow.feature.iuran.domain.usecase

import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIuranListUseCase @Inject constructor(
    private val repository: IuranRepository
) {
    operator fun invoke(bulan: Int, tahun: Int): Flow<List<Iuran>> =
        repository.observeIuranByMonth(bulan, tahun)
}