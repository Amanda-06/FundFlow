package com.example.fundflow.feature.pengeluaran.domain.usecase

import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPengeluaranListUseCase @Inject constructor(private val repo: PengeluaranRepository) {
    operator fun invoke(query: String = ""): Flow<List<Pengeluaran>> =
        if (query.isBlank()) repo.getAll() else repo.search(query)
}