package com.example.fundflow.feature.auth.data.repository

import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.feature.auth.data.local.UserDao
import com.example.fundflow.feature.auth.data.mapper.toDomain
import com.example.fundflow.feature.auth.data.mapper.toUserEntity
import com.example.fundflow.feature.auth.data.model.UserEntity
import com.example.fundflow.feature.auth.data.remote.FirebaseAuthDataSource
import com.example.fundflow.feature.auth.domain.model.User
import com.example.fundflow.feature.auth.domain.repository.AuthRepository

// IMPORT SELURUH DAO UNTUK MEMBERSIHKAN DATABASE SAAT LOGOUT
import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseAuthDataSource,
    private val userDao: UserDao,
    private val settingsDataStore: SettingsDataStore,

    private val anggotaDao: AnggotaDao,
    private val iuranDao: IuranDao,
    private val periodeDao: PeriodeDao,
    private val pemasukanDao: PemasukanDao,
    private val pengeluaranDao: PengeluaranDao
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        namaLengkap: String,
        username: String,
        namaOrganisasi: String
    ): User {
        val firebaseUser = remoteDataSource.register(
            email, password, namaLengkap, username, namaOrganisasi
        )
        val entity = UserEntity(
            userId         = firebaseUser.uid,
            namaLengkap    = namaLengkap,
            username       = username,
            email          = email,
            namaOrganisasi = namaOrganisasi
        )
        // Simpan ke Room lokal (offline-first)
        userDao.insertUser(entity)
        settingsDataStore.setLoggedIn(true)
        return entity.toDomain()
    }

    override suspend fun login(email: String, password: String): User {
        val firebaseUser = remoteDataSource.login(email, password)
        // Coba ambil dari Room dulu, jika tidak ada → fetch dari Firestore
        var entity = userDao.getUserById(firebaseUser.uid)
        if (entity == null) {
            val profileMap = remoteDataSource.fetchProfile(firebaseUser.uid)
            entity = profileMap?.toUserEntity()
                ?: UserEntity(
                    userId      = firebaseUser.uid,
                    namaLengkap = firebaseUser.displayName.orEmpty(),
                    username    = "",
                    email       = firebaseUser.email.orEmpty()
                )
            userDao.insertUser(entity)
        }
        settingsDataStore.setLoggedIn(true)
        return entity.toDomain()
    }

    override suspend fun logout() {
        // 1. Keluar dari sesi Firebase Cloud
        remoteDataSource.logout()

        // 2. Bersihkan total seluruh isi tabel Room DB lokal di HP demi mencegah data leak
        try {
            userDao.clearAll()
            periodeDao.deleteAllPeriode()
            anggotaDao.deleteAllAnggota()
            iuranDao.deleteAllIuran()
            pemasukanDao.deleteAllPemasukan()
            pengeluaranDao.deleteAllPengeluaran()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Set status login preference menjadi false
        settingsDataStore.setLoggedIn(false)
    }

    override fun observeCurrentUser(): Flow<User?> =
        userDao.observeCurrentUser().map { it?.toDomain() }

    override suspend fun getCurrentUser(): User? {
        val uid = remoteDataSource.currentFirebaseUser?.uid ?: return null
        return userDao.getUserById(uid)?.toDomain()
    }

    override suspend fun sendPasswordReset(email: String) =
        remoteDataSource.sendPasswordReset(email)

    override val isLoggedIn: Boolean
        get() = remoteDataSource.isLoggedIn
}