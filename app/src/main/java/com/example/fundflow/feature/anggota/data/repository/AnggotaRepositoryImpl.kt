package com.example.fundflow.feature.anggota.data.repository

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.anggota.data.model.AnggotaEntity // Menggunakan package Entity Anda yang benar
import com.example.fundflow.feature.anggota.data.mapper.toDomain
import com.example.fundflow.feature.anggota.data.mapper.toEntity
import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnggotaRepositoryImpl @Inject constructor(
    private val dao: AnggotaDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : AnggotaRepository {

    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override fun getAll(): Flow<List<Anggota>> =
        dao.getAllAnggota().map { list -> list.map { it.toDomain() } }

    override suspend fun add(anggota: Anggota): Long {
        // 1. Konversi ke Entity dan Simpan ke Room lokal (untuk dapat Auto-Increment ID)
        val entity = anggota.toEntity()
        val localId = dao.insertAnggota(entity)

        // 2. Sinkronisasi ke Firestore menggunakan nama field dari AnggotaEntity
        currentUserId?.let { userId ->
            val anggotaMap = mapOf(
                "anggotaId" to localId.toInt(),
                "namaAnggota" to entity.namaAnggota,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "anggota",
                documentId = localId.toString(),
                data = anggotaMap
            )
        }
        return localId
    }

    override suspend fun update(anggota: Anggota) {
        // 1. Konversi ke Entity dan Update lokal Room
        val entity = anggota.toEntity()
        dao.updateAnggota(entity)

        // 2. Update di Firestore Cloud
        currentUserId?.let { userId ->
            val anggotaMap = mapOf(
                "anggotaId" to entity.anggotaId,
                "namaAnggota" to entity.namaAnggota,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "anggota",
                documentId = entity.anggotaId.toString(),
                data = anggotaMap
            )
        }
    }

    override suspend fun delete(id: Int) {
        dao.deleteById(id)
        currentUserId?.let { userId ->
            firestoreService.deleteDocument(userId, "anggota", id.toString())
        }
    }

    override suspend fun deleteSelected(ids: List<Int>) {
        dao.deleteByIds(ids)
        currentUserId?.let { userId ->
            ids.forEach { id ->
                firestoreService.deleteDocument(userId, "anggota", id.toString())
            }
        }
    }

    override suspend fun getById(id: Int): Anggota? =
        dao.getAnggotaById(id)?.toDomain()

    /**
     * Fungsi Sinkronisasi: Membaca dari Cloud, lalu disimpan ke database lokal HP.
     * Panggil fungsi ini di init {} block pada AnggotaViewModel Anda.
     */
    suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            // Ambil data sub-koleksi 'anggota' milik user dari Firestore
            val cloudDataList = firestoreService.getCollection(userId, "anggota")

            // Petakan dari Map Firestore kembali menjadi list AnggotaEntity Room
            val localEntities = cloudDataList.map { map ->
                AnggotaEntity(
                    anggotaId = (map["anggotaId"] as? Long)?.toInt() ?: 0,
                    namaAnggota = map["namaAnggota"] as? String ?: "",
                    createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
                )
            }

            // Jika ada data dari cloud, masukkan ke Room lokal agar HP baru tersinkronisasi
            if (localEntities.isNotEmpty()) {
                dao.insertAllAnggota(localEntities) // Menggunakan fungsi batch baru
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}