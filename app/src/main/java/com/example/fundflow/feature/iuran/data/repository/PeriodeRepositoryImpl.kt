package com.example.fundflow.feature.iuran.data.repository

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.iuran.data.mapper.toDomain
import com.example.fundflow.feature.iuran.data.mapper.toEntity
import com.example.fundflow.feature.iuran.domain.model.Periode
import com.example.fundflow.feature.iuran.domain.repository.PeriodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PeriodeRepositoryImpl @Inject constructor(
    private val dao: PeriodeDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : PeriodeRepository {

    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override suspend fun savePeriode(periode: Periode): Long {
        // 1. Simpan ke database Room lokal
        val entity = periode.toEntity()
        val localId = dao.insertPeriode(entity)

        // 2. Kirim data ke sub-koleksi "periode" di Firestore Cloud
        currentUserId?.let { userId ->
            val periodeMap = mapOf(
                "periodeId" to localId.toInt(),
                "userId" to entity.userId,
                "tanggalMulai" to entity.tanggalMulai,
                "tanggalSelesai" to entity.tanggalSelesai,
                "createdAt" to entity.createdAt
            )

            // Menggunakan documentId "current_periode" karena relasi 1:1 per pengguna
            firestoreService.setDocument(
                userId = userId,
                collection = "periode",
                documentId = "current_periode",
                data = periodeMap
            )
        }
        return localId
    }

    override suspend fun getPeriodeByUserId(userId: String): Periode? =
        dao.getPeriodeByUserId(userId)?.toDomain()

    override fun observePeriode(userId: String): Flow<Periode?> =
        dao.observePeriode(userId).map { it?.toDomain() }

    override suspend fun deleteByUserId(userId: String) {
        dao.deleteByUserId(userId)
        currentUserId?.let { userIdCloud ->
            firestoreService.deleteDocument(userIdCloud, "periode", "current_periode")
        }
    }

    /**
     * Fungsi Sinkronisasi: Membaca data periode dari Firestore Cloud saat login di HP baru,
     * lalu menyimpannya ke database Room lokal HP tersebut.
     */
    override suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            // Ambil dokumen periode tunggal milik user dari cloud
            val cloudData = firestoreService.getDocument(userId, "periode", "current_periode")

            if (cloudData != null) {
                val entity = com.example.fundflow.feature.iuran.data.model.PeriodeEntity(
                    periodeId = (cloudData["periodeId"] as? Long)?.toInt() ?: 0,
                    userId = cloudData["userId"] as? String ?: userId,
                    tanggalMulai = cloudData["tanggalMulai"] as? String ?: "",
                    tanggalSelesai = cloudData["tanggalSelesai"] as? String ?: "",
                    createdAt = cloudData["createdAt"] as? Long ?: System.currentTimeMillis()
                )
                // Masukkan data hasil unduhan dari cloud ke Room lokal HP
                dao.insertPeriode(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}