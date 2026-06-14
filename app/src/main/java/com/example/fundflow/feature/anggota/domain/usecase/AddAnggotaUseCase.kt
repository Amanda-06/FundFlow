package com.example.fundflow.feature.anggota.domain.usecase

import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import javax.inject.Inject

class AddAnggotaUseCase @Inject constructor(
    private val repository: AnggotaRepository
) {
    suspend operator fun invoke(namaAnggota: String): Long =
        repository.add(Anggota(namaAnggota = namaAnggota.trim()))
}
