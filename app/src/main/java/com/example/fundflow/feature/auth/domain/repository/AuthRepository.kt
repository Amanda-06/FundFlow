package com.example.fundflow.feature.auth.domain.repository

import com.example.fundflow.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
        namaLengkap: String,
        username: String,
        namaOrganisasi: String
    ): User

    suspend fun login(email: String, password: String): User
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    fun observeCurrentUser(): Flow<User?>
    suspend fun sendPasswordReset(email: String)
    val isLoggedIn: Boolean
}