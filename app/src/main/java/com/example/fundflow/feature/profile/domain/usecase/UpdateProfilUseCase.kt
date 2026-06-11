package com.example.fundflow.feature.profile.domain.usecase

import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile) {
        require(profile.namaLengkap.isNotBlank()) { "Nama lengkap tidak boleh kosong" }
        require(profile.username.isNotBlank())    { "Username tidak boleh kosong" }
        repository.updateProfile(
            profile.copy(
                namaLengkap    = profile.namaLengkap.trim(),
                username       = profile.username.trim(),
                namaOrganisasi = profile.namaOrganisasi.trim()
            )
        )
    }
}