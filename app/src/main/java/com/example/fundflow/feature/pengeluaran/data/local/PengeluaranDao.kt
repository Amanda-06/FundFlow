package com.example.fundflow.feature.pengeluaran.data.local

import androidx.room.*
import com.example.fundflow.feature.pengeluaran.data.model.PengeluaranEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PengeluaranDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pengeluaran: PengeluaranEntity): Long

    // TAMBAHAN: Fungsi Insert Massal untuk Sinkronisasi Cloud yang Efisien
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pengeluaranList: List<PengeluaranEntity>)

    @Update
    suspend fun update(pengeluaran: PengeluaranEntity)

    @Query("SELECT * FROM pengeluaran ORDER BY tanggal DESC, created_at DESC")
    fun getAll(): Flow<List<PengeluaranEntity>>

    @Query("""
        SELECT * FROM pengeluaran
        WHERE deskripsi    LIKE '%' || :query || '%'
           OR kategori     LIKE '%' || :query || '%'
           OR nama_program LIKE '%' || :query || '%'
        ORDER BY tanggal DESC
    """)
    fun search(query: String): Flow<List<PengeluaranEntity>>

    @Query("SELECT * FROM pengeluaran WHERE pengeluaran_id = :id LIMIT 1")
    suspend fun getById(id: Int): PengeluaranEntity?

    @Query("DELETE FROM pengeluaran WHERE pengeluaran_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM pengeluaran WHERE pengeluaran_id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    // ── Agregasi ─────────────────────────────────────────────
    @Query("SELECT SUM(total_nominal) FROM pengeluaran")
    fun getTotalPengeluaran(): Flow<Double?>

    @Query("""
        SELECT * FROM pengeluaran
        ORDER BY tanggal DESC, created_at DESC
        LIMIT :limit
    """)
    fun getRecentPengeluaran(limit: Int = 5): Flow<List<PengeluaranEntity>>

    @Query("""
        SELECT * FROM pengeluaran
        WHERE tanggal BETWEEN :startDate AND :endDate
        ORDER BY tanggal DESC
    """)
    suspend fun getByDateRange(startDate: String, endDate: String): List<PengeluaranEntity>

    @Query("""
        SELECT SUM(total_nominal) FROM pengeluaran
        WHERE tanggal BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalByDateRange(startDate: String, endDate: String): Double?

    @Query("DELETE FROM pengeluaran")
    suspend fun deleteAllPengeluaran()
}