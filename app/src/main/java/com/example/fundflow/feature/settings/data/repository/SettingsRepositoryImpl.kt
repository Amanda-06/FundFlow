package com.example.fundflow.feature.settings.data.repository

import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.core.firebase.FirebaseAuthService // TAMBAHAN IMPORT
import com.example.fundflow.core.firebase.FirestoreService // TAMBAHAN IMPORT
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.iuran.data.model.PeriodeEntity
import com.example.fundflow.feature.settings.domain.model.AppSettings
import com.example.fundflow.feature.settings.domain.model.PeriodeKas
import com.example.fundflow.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val periodeDao: PeriodeDao,
    private val firestoreService: FirestoreService, // TAMBAHAN INJECT
    private val authService: FirebaseAuthService    // TAMBAHAN INJECT
) : SettingsRepository {

    private val currentUserId: String?
        get() = authService.currentUser?.uid

    override fun observeSettings(): Flow<AppSettings> = combine(
        settingsDataStore.isDarkTheme,
        settingsDataStore.language,
        settingsDataStore.isNotificationEnabled
    ) { dark, lang, notif ->
        AppSettings(isDarkTheme = dark, language = lang, isNotificationEnabled = notif)
    }

    override suspend fun setDarkTheme(enabled: Boolean) =
        settingsDataStore.setDarkTheme(enabled)

    override suspend fun setLanguage(lang: String) =
        settingsDataStore.setLanguage(lang)

    override suspend fun setNotificationEnabled(enabled: Boolean) =
        settingsDataStore.setNotificationEnabled(enabled)

    override fun observePeriode(userId: String): Flow<PeriodeKas?> =
        periodeDao.observePeriode(userId).map { entity ->
            entity?.let {
                PeriodeKas(
                    periodeId    = it.periodeId,
                    userId       = it.userId,
                    bulanMulai   = it.tanggalMulai,
                    bulanSelesai = it.tanggalSelesai
                )
            }
        }

    override suspend fun getPeriode(userId: String): PeriodeKas? =
        periodeDao.getPeriodeByUserId(userId)?.let {
            PeriodeKas(
                periodeId    = it.periodeId,
                userId       = it.userId,
                bulanMulai   = it.tanggalMulai,
                bulanSelesai = it.tanggalSelesai
            )
        }

    override suspend fun updatePeriode(periode: PeriodeKas) {
        val existing = periodeDao.getPeriodeByUserId(periode.userId)
        val entity = PeriodeEntity(
            periodeId      = existing?.periodeId ?: 0,
            userId         = periode.userId,
            tanggalMulai   = periode.bulanMulai,
            tanggalSelesai = periode.bulanSelesai,
            createdAt      = existing?.createdAt ?: System.currentTimeMillis()
        )

        // 1. Simpan ke database Room lokal HP
        periodeDao.insertPeriode(entity)

        // 2. Unggah perubahan baru ke Cloud Firestore secara Real-time
        currentUserId?.let { userIdCloud ->
            val periodeMap = mapOf(
                "periodeId" to entity.periodeId,
                "userId" to entity.userId,
                "tanggalMulai" to entity.tanggalMulai,
                "tanggalSelesai" to entity.tanggalSelesai,
                "createdAt" to entity.createdAt
            )

            firestoreService.setDocument(
                userId = userIdCloud,
                collection = "periode",
                documentId = "current_periode",
                data = periodeMap
            )
        }
    }

    /**
     * FUNGSI SINKRONISASI BARU: Membaca data periode dari Firestore Cloud khusus untuk halaman Settings,
     * lalu menyimpannya ke database Room lokal HP.
     */
    suspend fun syncWithCloud() {
        val userId = currentUserId ?: return
        try {
            val cloudData = firestoreService.getDocument(userId, "periode", "current_periode")
            if (cloudData != null) {
                val entity = PeriodeEntity(
                    periodeId = (cloudData["periodeId"] as? Long)?.toInt() ?: 0,
                    userId = cloudData["userId"] as? String ?: userId,
                    tanggalMulai = cloudData["tanggalMulai"] as? String ?: "",
                    tanggalSelesai = cloudData["tanggalSelesai"] as? String ?: "",
                    createdAt = cloudData["createdAt"] as? Long ?: System.currentTimeMillis()
                )
                periodeDao.insertPeriode(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}