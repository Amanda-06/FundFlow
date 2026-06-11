package com.example.fundflow.feature.pemasukan.data.local

import androidx.room.*
import com.example.fundflow.feature.pemasukan.data.model.PemasukanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PemasukanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pemasukan: PemasukanEntity): Long

    @Update
    suspend fun update(pemasukan: PemasukanEntity)

    @Query("SELECT * FROM pemasukan ORDER BY tanggal DESC, created_at DESC")
    fun getAll(): Flow<List<PemasukanEntity>>

    @Query("""
        SELECT * FROM pemasukan
        WHERE deskripsi LIKE '%' || :query || '%'
           OR sumber     LIKE '%' || :query || '%'
        ORDER BY tanggal DESC
    """)
    fun search(query: String): Flow<List<PemasukanEntity>>

    @Query("SELECT * FROM pemasukan WHERE pemasukan_id = :id LIMIT 1")
    suspend fun getById(id: Int): PemasukanEntity?

    @Query("DELETE FROM pemasukan WHERE pemasukan_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM pemasukan WHERE pemasukan_id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    // ── Agregasi untuk Dashboard ──────────────────────────────
    @Query("SELECT SUM(nominal) FROM pemasukan")
    fun getTotalPemasukan(): Flow<Double?>

    @Query("""
        SELECT * FROM pemasukan
        ORDER BY tanggal DESC, created_at DESC
        LIMIT :limit
    """)
    fun getRecentPemasukan(limit: Int = 5): Flow<List<PemasukanEntity>>

    // ── Untuk laporan periode ─────────────────────────────────
    @Query("""
        SELECT * FROM pemasukan
        WHERE tanggal BETWEEN :startDate AND :endDate
        ORDER BY tanggal DESC
    """)
    suspend fun getByDateRange(startDate: String, endDate: String): List<PemasukanEntity>

    @Query("""
        SELECT SUM(nominal) FROM pemasukan
        WHERE tanggal BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalByDateRange(startDate: String, endDate: String): Double?
}