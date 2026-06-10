package com.example.fundflow.feature.auth.domain.usecase

import com.example.fundflow.feature.auth.domain.model.User
import com.example.fundflow.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User =
        repository.login(email.trim(), password)
}