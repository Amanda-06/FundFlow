package com.example.fundflow.feature.pengeluaran.data.repository

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.feature.pengeluaran.data.model.PengeluaranEntity // Pastikan package Entity Anda sesuai
import com.example.fundflow.feature.pengeluaran.data.mapper.toDomain
import com.example.fundflow.feature.pengeluaran.data.mapper.toDomainList
import com.example.fundflow.feature.pengeluaran.data.mapper.toEntity
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PengeluaranRepositoryImpl @Inject constructor(
    private val dao: PengeluaranDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : PengeluaranRepository {

    // Helper untuk mendapatkan UID pengguna yang sedang login
    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override fun getAll(): Flow<List<Pengeluaran>> =
        dao.getAll().map { it.toDomainList() }

    override fun search(query: String): Flow<List<Pengeluaran>> =
        dao.search(query).map { it.toDomainList() }

    override suspend fun add(p: Pengeluaran): Long {
        // 1. Simpan ke lokal SQLite (Room) untuk menghasilkan auto-increment ID
        val entity = p.toEntity()
        val localId = dao.insert(entity)

        // 2. Jika user login, sinkronisasikan ke sub-koleksi "pengeluaran" di Firestore
        currentUserId?.let { userId ->
            val pengeluaranMap = mapOf(
                "pengeluaranId" to localId.toInt(),
                "deskripsi" to entity.deskripsi,
                "kategori" to entity.kategori,
                "namaProgram" to entity.namaProgram,
                "metode" to entity.metode,
                "quantity" to entity.quantity,
                "hargaSatuan" to entity.hargaSatuan,
                "totalNominal" to entity.totalNominal,
                "tanggal" to entity.tanggal,
                "catatan" to entity.catatan,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "pengeluaran",
                documentId = localId.toString(),
                data = pengeluaranMap
            )
        }
        return localId
    }

    override suspend fun update(p: Pengeluaran) {
        // 1. Update ke lokal Room
        val entity = p.toEntity()
        dao.update(entity)

        // 2. Update data di Firestore Cloud
        currentUserId?.let { userId ->
            val pengeluaranMap = mapOf(
                "pengeluaranId" to entity.pengeluaranId,
                "deskripsi" to entity.deskripsi,
                "kategori" to entity.kategori,
                "namaProgram" to entity.namaProgram,
                "metode" to entity.metode,
                "quantity" to entity.quantity,
                "hargaSatuan" to entity.hargaSatuan,
                "totalNominal" to entity.totalNominal,
                "tanggal" to entity.tanggal,
                "catatan" to entity.catatan,
                "createdAt" to entity.createdAt
            )
            firestoreService.setDocument(
                userId = userId,
                collection = "pengeluaran",
                documentId = entity.pengeluaranId.toString(),
                data = pengeluaranMap
            )
        }
    }

    override suspend fun delete(id: Int) {
        // 1. Hapus di lokal Room
        dao.deleteById(id)

        // 2. Hapus dokumen di Firestore Cloud
        currentUserId?.let { userId ->
            firestoreService.deleteDocument(userId, "pengeluaran", id.toString())
        }
    }

    override suspend fun deleteSelected(ids: List<Int>) {
        // 1. Hapus kumpulan data di lokal Room
        dao.deleteByIds(ids)

        // 2. Hapus dokumen satu per satu di Firestore Cloud
        currentUserId?.let { userId ->
            ids.forEach { id ->
                firestoreService.deleteDocument(userId, "pengeluaran", id.toString())
            }
        }
    }

    override suspend fun getById(id: Int): Pengeluaran? =
        dao.getById(id)?.toDomain()

    override suspend fun getByDateRange(s: String, e: String): List<Pengeluaran> =
        dao.getByDateRange(s, e).toDomainList()

    suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            // Mengambil seluruh dokumen dari sub-koleksi 'pengeluaran' milik pengguna
            val cloudDataList = firestoreService.getCollection(userId, "pengeluaran")

            // Konversi List<Map> menjadi List<PengeluaranEntity> dengan penanganan tipe data yang aman
            val localEntities = cloudDataList.map { map ->
                PengeluaranEntity(
                    pengeluaranId = (map["pengeluaranId"] as? Long)?.toInt() ?: 0,
                    deskripsi = map["deskripsi"] as? String ?: "",
                    kategori = map["kategori"] as? String ?: "",
                    namaProgram = map["namaProgram"] as? String ?: "",
                    metode = map["metode"] as? String ?: "",
                    quantity = (map["quantity"] as? Long)?.toInt() ?: 1,
                    hargaSatuan = (map["hargaSatuan"] as? Number)?.toDouble() ?: 0.0,
                    totalNominal = (map["totalNominal"] as? Number)?.toDouble() ?: 0.0,
                    tanggal = map["tanggal"] as? String ?: "",
                    catatan = map["catatan"] as? String ?: "",
                    createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
                )
            }

            // Masukkan data cloud ke Room lokal HP jika list tidak kosong
            if (localEntities.isNotEmpty()) {
                dao.insertAll(localEntities) // Menggunakan fungsi batch baru
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}