package com.example.fundflow.feature.profile.data.remote

import com.example.fundflow.core.firebase.FirestoreService
import javax.inject.Inject

/**
 * Sumber data remote untuk profil — sinkronisasi ke Firestore.
 * Path: users/{userId}
 */
class ProfileRemoteDataSource @Inject constructor(
    private val firestoreService: FirestoreService
) {
    suspend fun fetchProfile(userId: String): Map<String, Any>? =
        firestoreService.getProfile(userId)

    suspend fun saveProfile(userId: String, data: Map<String, Any>) =
        firestoreService.saveProfile(userId, data)
}