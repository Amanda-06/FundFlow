package com.example.fundflow.feature.pemasukan.domain.usecase

import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import javax.inject.Inject

class DeletePemasukanUseCase @Inject constructor(
    private val repository: PemasukanRepository
) {
    suspend operator fun invoke(id: Int) = repository.delete(id)
}