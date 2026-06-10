package com.example.fundflow.feature.auth.domain.usecase

import com.example.fundflow.feature.auth.domain.model.User
import com.example.fundflow.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        namaLengkap: String,
        username: String,
        namaOrganisasi: String
    ): User = repository.register(
        email          = email.trim(),
        password       = password,
        namaLengkap    = namaLengkap.trim(),
        username       = username.trim(),
        namaOrganisasi = namaOrganisasi.trim()
    )
}