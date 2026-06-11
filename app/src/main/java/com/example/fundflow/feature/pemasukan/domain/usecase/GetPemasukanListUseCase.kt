package com.example.fundflow.feature.pemasukan.domain.usecase

import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPemasukanListUseCase @Inject constructor(
    private val repository: PemasukanRepository
) {
    operator fun invoke(query: String = ""): Flow<List<Pemasukan>> =
        if (query.isBlank()) repository.getAll() else repository.search(query)
}