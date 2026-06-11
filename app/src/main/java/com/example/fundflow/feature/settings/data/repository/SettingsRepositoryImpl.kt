package com.example.fundflow.feature.settings.data.repository

import com.example.fundflow.core.datastore.SettingsDataStore
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
    private val periodeDao: PeriodeDao
) : SettingsRepository {

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
        periodeDao.insertPeriode(
            PeriodeEntity(
                periodeId      = existing?.periodeId ?: 0,
                userId         = periode.userId,
                tanggalMulai   = periode.bulanMulai,
                tanggalSelesai = periode.bulanSelesai,
                createdAt      = existing?.createdAt ?: System.currentTimeMillis()
            )
        )
    }
}