package com.example.fundflow.feature.pemasukan.domain.usecase

import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import javax.inject.Inject

class AddPemasukanUseCase @Inject constructor(
    private val repository: PemasukanRepository
) {
    suspend operator fun invoke(pemasukan: Pemasukan): Long =
        repository.add(pemasukan)
}