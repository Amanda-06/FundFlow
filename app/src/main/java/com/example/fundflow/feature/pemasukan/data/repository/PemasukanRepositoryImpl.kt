package com.example.fundflow.feature.pemasukan.data.repository

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pemasukan.data.model.PemasukanEntity // Menggunakan package Entity Anda yang benar
import com.example.fundflow.feature.pemasukan.data.mapper.toDomain
import com.example.fundflow.feature.pemasukan.data.mapper.toDomainList
import com.example.fundflow.feature.pemasukan.data.mapper.toEntity
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PemasukanRepositoryImpl @Inject constructor(
    private val dao: PemasukanDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : PemasukanRepository {

    // Helper untuk mendapatkan UID pengguna yang sedang login
    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override fun getAll(): Flow<List<Pemasukan>> =
        dao.getAll().map { it.toDomainList() }

    override fun search(query: String): Flow<List<Pemasukan>> =
        dao.search(query).map { it.toDomainList() }

    override suspend fun add(pemasukan: Pemasukan): Long {
        // 1. Simpan ke lokal SQLite (Room) untuk menghasilkan auto-increment ID
        val entity = pemasukan.toEntity()
        val localId = dao.insert(entity)

        // 2. Jika user berhasil login, sinkronisasikan ke Firestore Cloud
        currentUserId?.let { userId ->
            val pemasukanMap = mapOf(
                "pemasukanId" to localId.toInt(),
                "deskripsi" to entity.deskripsi,
                "sumber" to entity.sumber,
                "metode" to entity.metode,
                "nominal" to entity.nominal,
                "tanggal" to entity.tanggal,
                "catatan" to entity.catatan,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "pemasukan",
                documentId = localId.toString(),
                data = pemasukanMap
            )
        }
        return localId
    }

    override suspend fun update(pemasukan: Pemasukan) {
        // 1. Update ke lokal Room
        val entity = pemasukan.toEntity()
        dao.update(entity)

        // 2. Update data lama di Firestore Cloud
        currentUserId?.let { userId ->
            val pemasukanMap = mapOf(
                "pemasukanId" to entity.pemasukanId,
                "deskripsi" to entity.deskripsi,
                "sumber" to entity.sumber,
                "metode" to entity.metode,
                "nominal" to entity.nominal,
                "tanggal" to entity.tanggal,
                "catatan" to entity.catatan,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "pemasukan",
                documentId = entity.pemasukanId.toString(),
                data = pemasukanMap
            )
        }
    }

    override suspend fun delete(id: Int) {
        // 1. Hapus di lokal Room
        dao.deleteById(id)

        // 2. Hapus dokumen di Firestore Cloud
        currentUserId?.let { userId ->
            firestoreService.deleteDocument(userId, "pemasukan", id.toString())
        }
    }

    override suspend fun deleteSelected(ids: List<Int>) {
        // 1. Hapus kumpulan data terpilih di lokal Room
        dao.deleteByIds(ids)

        // 2. Hapus dokumen satu per satu di Firestore Cloud
        currentUserId?.let { userId ->
            ids.forEach { id ->
                firestoreService.deleteDocument(userId, "pemasukan", id.toString())
            }
        }
    }

    override suspend fun getById(id: Int): Pemasukan? =
        dao.getById(id)?.toDomain()

    override suspend fun getByDateRange(
        startDate: String,
        endDate: String
    ): List<Pemasukan> = dao.getByDateRange(startDate, endDate).toDomainList()

    /**
     * Fungsi Sinkronisasi: Mengunduh data dari sub-koleksi 'pemasukan' di Firestore,
     * lalu menyimpannya ke database Room internal HP.
     * Panggil fungsi ini di dalam `init { }` pada PemasukanViewModel Anda.
     */
    suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            // Ambil semua data sub-koleksi pemasukan milik user dari cloud
            val cloudDataList = firestoreService.getCollection(userId, "pemasukan")

            // Konversi List<Map> menjadi List<PemasukanEntity>
            val localEntities = cloudDataList.map { map ->
                PemasukanEntity(
                    pemasukanId = (map["pemasukanId"] as? Long)?.toInt() ?: 0,
                    deskripsi = map["deskripsi"] as? String ?: "",
                    sumber = map["sumber"] as? String ?: "",
                    metode = map["metode"] as? String ?: "",
                    // Gunakan Number cast agar aman jika Firestore mengembalikan Double bulat sebagai Long
                    nominal = (map["nominal"] as? Number)?.toDouble() ?: 0.0,
                    tanggal = map["tanggal"] as? String ?: "",
                    catatan = map["catatan"] as? String ?: "",
                    createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
                )
            }

            // Simpan data cloud tersebut ke Room database lokal HP jika tidak kosong
            if (localEntities.isNotEmpty()) {
                dao.insertAll(localEntities) // Menggunakan fungsi batch baru
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}