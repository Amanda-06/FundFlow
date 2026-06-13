// ============================================================
// feature/anggota/data/local/AnggotaDao.kt
// ============================================================
package com.example.fundflow.feature.anggota.data.local

import androidx.room.*
import com.example.fundflow.feature.anggota.data.model.AnggotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnggotaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnggota(anggota: AnggotaEntity): Long

    // TAMBAHAN: Fungsi Insert Massal untuk Sinkronisasi Cloud yang Efisien
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnggota(anggotaList: List<AnggotaEntity>)

    @Update
    suspend fun updateAnggota(anggota: AnggotaEntity)

    @Query("SELECT * FROM anggota ORDER BY nama_anggota ASC")
    fun getAllAnggota(): Flow<List<AnggotaEntity>>

    @Query("SELECT * FROM anggota WHERE anggota_id = :id LIMIT 1")
    suspend fun getAnggotaById(id: Int): AnggotaEntity?

    @Query("DELETE FROM anggota WHERE anggota_id = :id")
    suspend fun deleteById(id: Int)

    /** Batch delete — dipakai oleh DeleteSelectedAnggotaUseCase */
    @Query("DELETE FROM anggota WHERE anggota_id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM anggota")
    suspend fun countAnggota(): Int

    @Query("DELETE FROM anggota")
    suspend fun deleteAllAnggota()
}