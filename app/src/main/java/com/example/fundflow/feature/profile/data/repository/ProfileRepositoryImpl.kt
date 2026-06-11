package com.example.fundflow.feature.profile.data.repository

import com.example.fundflow.feature.auth.data.local.UserDao
import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.feature.profile.data.mapper.toEntity
import com.example.fundflow.feature.profile.data.mapper.toFirestoreMap
import com.example.fundflow.feature.profile.data.mapper.toProfile
import com.example.fundflow.feature.profile.data.remote.ProfileRemoteDataSource
import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val remoteDataSource: ProfileRemoteDataSource,
    private val authService: FirebaseAuthService
) : ProfileRepository {

    /** Observe profil dari Room (offline-first) */
    override fun observeProfile(): Flow<Profile?> =
        userDao.observeCurrentUser().map { it?.toProfile() }

    override suspend fun getProfile(): Profile? {
        val uid = authService.currentUser?.uid ?: return null
        // Coba ambil dari Room dulu
        var entity = userDao.getUserById(uid)
        if (entity == null) {
            // Fallback: ambil dari Firestore lalu cache ke Room
            val remoteData = remoteDataSource.fetchProfile(uid)
            if (remoteData != null) {
                entity = com.example.fundflow.feature.auth.data.model.UserEntity(
                    userId         = uid,
                    namaLengkap    = (remoteData["nama_lengkap"] as? String).orEmpty(),
                    username       = (remoteData["username"] as? String).orEmpty(),
                    email          = (remoteData["email"] as? String).orEmpty(),
                    namaOrganisasi = (remoteData["nama_organisasi"] as? String).orEmpty()
                )
                userDao.insertUser(entity)
            }
        }
        return entity?.toProfile()
    }

    /**
     * Update profil — simpan ke Room (offline-first) sekaligus sync ke Firestore.
     */
    override suspend fun updateProfile(profile: Profile) {
        val existing  = userDao.getUserById(profile.userId)
        val createdAt = existing?.createdAt ?: System.currentTimeMillis()

        // 1. Simpan ke Room — sumber kebenaran lokal
        userDao.insertUser(profile.toEntity(createdAt))

        // 2. Sync ke Firestore (best-effort, tidak memblokir UI jika gagal)
        runCatching {
            remoteDataSource.saveProfile(profile.userId, profile.toFirestoreMap())
        }
    }
}