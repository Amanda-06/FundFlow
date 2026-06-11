package com.example.fundflow.feature.laporan.data.local

import androidx.room.*
import com.example.fundflow.feature.laporan.data.model.LaporanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: LaporanEntity): Long

    @Query("SELECT * FROM laporan ORDER BY tanggal_generate DESC")
    fun getAll(): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE laporan_id = :id LIMIT 1")
    suspend fun getById(id: Int): LaporanEntity?

    @Query("DELETE FROM laporan WHERE laporan_id = :id")
    suspend fun deleteById(id: Int)
}