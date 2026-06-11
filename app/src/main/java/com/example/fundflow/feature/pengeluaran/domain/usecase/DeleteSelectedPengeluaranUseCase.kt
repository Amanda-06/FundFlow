package com.example.fundflow.feature.pengeluaran.domain.usecase

import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DeleteSelectedPengeluaranUseCase @Inject constructor(private val repo: PengeluaranRepository) {
    suspend operator fun invoke(ids: List<Int>) = repo.deleteSelected(ids)
}