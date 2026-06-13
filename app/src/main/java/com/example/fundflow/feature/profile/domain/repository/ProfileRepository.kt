// feature/profile/domain/repository/ProfileRepository.kt
package com.example.fundflow.feature.profile.domain.repository

import com.example.fundflow.feature.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile?>
    suspend fun getProfile(): Profile?
    suspend fun updateProfile(profile: Profile)

    /**
     * Re-autentikasi dengan [passwordSaatIni], lalu update password ke [passwordBaru]
     * via Firebase Auth. Melempar Exception jika password lama salah.
     */
    suspend fun updatePassword(passwordSaatIni: String, passwordBaru: String)
}
