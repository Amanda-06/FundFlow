package com.example.fundflow.feature.profile.domain.repository

import com.example.fundflow.feature.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile?>
    suspend fun getProfile(): Profile?
    suspend fun updateProfile(profile: Profile)
}