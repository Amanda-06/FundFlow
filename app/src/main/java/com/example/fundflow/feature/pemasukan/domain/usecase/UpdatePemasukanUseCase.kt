// feature/pemasukan/domain/usecase/UpdatePemasukanUseCase.kt
package com.example.fundflow.feature.pemasukan.domain.usecase

import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import javax.inject.Inject

class UpdatePemasukanUseCase @Inject constructor(
    private val repository: PemasukanRepository
) {
    suspend operator fun invoke(pemasukan: Pemasukan) =
        // Langsung update karena nominal sudah diisi langsung dari UI
        repository.update(pemasukan)
}