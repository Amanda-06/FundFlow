package com.example.fundflow.feature.pengeluaran.domain.usecase

import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdatePengeluaranUseCase @Inject constructor(private val repo: PengeluaranRepository) {
    suspend operator fun invoke(p: Pengeluaran) =
        repo.update(p.copy(totalNominal = p.quantity * p.hargaSatuan))
}