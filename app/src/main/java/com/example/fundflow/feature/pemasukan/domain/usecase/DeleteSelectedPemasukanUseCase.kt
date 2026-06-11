package com.example.fundflow.feature.pemasukan.domain.usecase

import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import javax.inject.Inject

class DeleteSelectedPemasukanUseCase @Inject constructor(
    private val repository: PemasukanRepository
) {
    suspend operator fun invoke(ids: List<Int>) = repository.deleteSelected(ids)
}