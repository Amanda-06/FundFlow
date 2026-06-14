package com.example.fundflow.feature.profile.data.repository

import com.example.fundflow.feature.auth.data.local.UserDao
import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.feature.profile.data.mapper.toEntity
import com.example.fundflow.feature.profile.data.mapper.toFirestoreMap
import com.example.fundflow.feature.profile.data.mapper.toProfile
import com.example.fundflow.feature.profile.data.remote.ProfileRemoteDataSource
import com.example.fundflow.feature.profile.domain.model.Profile
import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
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
        var entity = userDao.getUserById(uid)
        if (entity == null) {
            val remoteData = remoteDataSource.fetchProfile(uid)
            if (remoteData != null) {
                entity = com.example.fundflow.feature.auth.data.model.UserEntity(
                    userId         = uid,
                    namaLengkap    = ((remoteData["nama_lengkap"] ?: remoteData["namaLengkap"]) as? String).orEmpty(),
                    username       = (remoteData["username"] as? String).orEmpty(),
                    email          = (remoteData["email"] as? String).orEmpty(),
                    namaOrganisasi = ((remoteData["nama_organisasi"] ?: remoteData["namaOrganisasi"]) as? String).orEmpty()
                )
                userDao.insertUser(entity)
            }
        }
        return entity?.toProfile()
    }

    /** Update profil — simpan ke Room (offline-first) lalu sync ke Firestore. */
    override suspend fun updateProfile(profile: Profile) {
        val existing  = userDao.getUserById(profile.userId)
        val createdAt = existing?.createdAt ?: System.currentTimeMillis()

        userDao.insertUser(profile.toEntity(createdAt))

        runCatching {
            remoteDataSource.saveProfile(profile.userId, profile.toFirestoreMap())
        }
    }

    /**
     * Re-autentikasi dengan password lama, lalu update ke password baru via Firebase Auth.
     */
    override suspend fun updatePassword(passwordSaatIni: String, passwordBaru: String) {
        val user  = authService.currentUser
            ?: throw IllegalStateException("Pengguna belum login")
        val email = user.email
            ?: throw IllegalStateException("Email pengguna tidak ditemukan")

        val credential = EmailAuthProvider.getCredential(email, passwordSaatIni)
        user.reauthenticate(credential).await()

        user.updatePassword(passwordBaru).await()
    }

    suspend fun syncWithCloud() {
        val uid = authService.currentUser?.uid ?: return
        try {
            val remoteData = remoteDataSource.fetchProfile(uid)
            if (remoteData != null) {
                val existing = userDao.getUserById(uid)
                val createdAt = existing?.createdAt ?: System.currentTimeMillis()

                val entity = com.example.fundflow.feature.auth.data.model.UserEntity(
                    userId         = uid,
                    namaLengkap    = ((remoteData["nama_lengkap"] ?: remoteData["namaLengkap"]) as? String).orEmpty(),
                    username       = (remoteData["username"] as? String).orEmpty(),
                    email          = (remoteData["email"] as? String).orEmpty(),
                    namaOrganisasi = ((remoteData["nama_organisasi"] ?: remoteData["namaOrganisasi"]) as? String).orEmpty(),
                    createdAt      = createdAt
                )
                userDao.insertUser(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}