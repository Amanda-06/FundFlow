package com.example.fundflow.feature.iuran.data.repository

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.data.mapper.toDomain
import com.example.fundflow.feature.iuran.data.model.IuranEntity
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.model.IuranSummary
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IuranRepositoryImpl @Inject constructor(
    private val dao: IuranDao,
    private val firestoreService: FirestoreService, // Tambahkan ini
    private val authService: FirebaseAuthService    // Tambahkan ini
) : IuranRepository {

    // Helper untuk mengambil UID user yang sedang login
    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override fun observeIuranByMonth(bulan: Int, tahun: Int): Flow<List<Iuran>> =
        dao.observeIuranByMonth(bulan, tahun).map { rows ->
            rows.map { it.toDomain(bulan, tahun) }
        }

    override fun observeSummary(bulan: Int, tahun: Int): Flow<IuranSummary> =
        dao.getIuranSummaryByMonth(bulan, tahun).map { raw ->
            raw?.toDomain() ?: IuranSummary()
        }

    override suspend fun saveIuran(iuran: Iuran) {
        // 1. Cari record existing untuk dapatkan iuran_id (jika ada) agar REPLACE tepat sasaran
        val existing = dao.getIuranByAnggotaAndMonth(iuran.anggotaId, iuran.bulan, iuran.tahun)

        val entity = IuranEntity(
            iuranId = existing?.iuranId ?: 0,
            anggotaId = iuran.anggotaId,
            bulan = iuran.bulan,
            tahun = iuran.tahun,
            nominal = iuran.nominal,
            statusBayar = iuran.statusBayar,
            terlambat = iuran.terlambat,
            metodePembayaran = iuran.metodePembayaran,
            tanggalBayar = iuran.tanggalBayar,
            catatan = iuran.catatan,
            createdAt = existing?.createdAt ?: System.currentTimeMillis()
        )

        // 2. Simpan ke database Room lokal
        dao.upsertIuran(entity)

        // 3. Sinkronisasikan ke Firestore Cloud
        currentUserId?.let { userId ->
            // Ambil kembali data yang benar-benar tersimpan di Room untuk memastikan iuranId-nya valid
            val savedEntity = dao.getIuranByAnggotaAndMonth(iuran.anggotaId, iuran.bulan, iuran.tahun)

            if (savedEntity != null) {
                val iuranMap = mapOf(
                    "iuranId" to savedEntity.iuranId,
                    "anggotaId" to savedEntity.anggotaId,
                    "bulan" to savedEntity.bulan,
                    "tahun" to savedEntity.tahun,
                    "nominal" to savedEntity.nominal,
                    "statusBayar" to savedEntity.statusBayar,
                    "terlambat" to savedEntity.terlambat,
                    "metodePembayaran" to savedEntity.metodePembayaran,
                    "tanggalBayar" to savedEntity.tanggalBayar, // Bisa bernilai null
                    "catatan" to savedEntity.catatan,
                    "createdAt" to savedEntity.createdAt
                )

                // Simpan ke sub-koleksi users/{userId}/iuran/{iuranId}
                firestoreService.setDocument(
                    userId = userId,
                    collection = "iuran",
                    documentId = savedEntity.iuranId.toString(),
                    data = iuranMap
                )
            }
        }
    }

    /**
     * FUNGSI SINKRONISASI BARU: Membaca seluruh data iuran dari Firestore Cloud,
     * lalu menyimpannya ke database lokal Room HP.
     * Panggil fungsi ini di dalam `init {}` block IuranViewModel Anda.
     */
    suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            // Ambil seluruh dokumen dari sub-koleksi 'iuran' milik user di cloud
            val cloudDataList = firestoreService.getCollection(userId, "iuran")

            // Konversi dari List<Map> menjadi List<IuranEntity> dengan penanganan tipe data aman
            val localEntities = cloudDataList.map { map ->
                IuranEntity(
                    iuranId = (map["iuranId"] as? Long)?.toInt() ?: 0,
                    anggotaId = (map["anggotaId"] as? Long)?.toInt() ?: 0,
                    bulan = (map["bulan"] as? Long)?.toInt() ?: 1,
                    tahun = (map["tahun"] as? Long)?.toInt() ?: 2026,
                    nominal = (map["nominal"] as? Number)?.toDouble() ?: 0.0,
                    statusBayar = map["statusBayar"] as? Boolean ?: false,
                    terlambat = map["terlambat"] as? Boolean ?: false,
                    metodePembayaran = map["metodePembayaran"] as? String ?: "",
                    tanggalBayar = map["tanggalBayar"] as? String, // String opsional/nullable
                    catatan = map["catatan"] as? String ?: "",
                    createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
                )
            }

            // Masukkan data hasil unduhan dari cloud ke dalam database Room lokal HP
            if (localEntities.isNotEmpty()) {
                localEntities.forEach { entity ->
                    dao.upsertIuran(entity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}