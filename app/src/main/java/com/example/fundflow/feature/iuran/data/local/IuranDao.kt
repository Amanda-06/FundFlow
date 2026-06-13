package com.example.fundflow.feature.iuran.data.local

import androidx.room.*
import com.example.fundflow.feature.iuran.data.model.IuranAnggotaRow
import com.example.fundflow.feature.iuran.data.model.IuranEntity
import com.example.fundflow.feature.iuran.data.model.IuranSummaryRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface IuranDao {

    /**
     * Upsert iuran. Karena ada unique index (anggota_id, bulan, tahun),
     * REPLACE akan otomatis mengganti record yang sudah ada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIuran(iuran: IuranEntity): Long

    /**
     * Daftar seluruh anggota beserta status iuran mereka pada bulan/tahun
     * tertentu (LEFT JOIN — anggota tanpa record iuran tetap muncul
     * dengan kolom iuran bernilai null = dianggap belum bayar).
     * Reaktif: otomatis update saat ada perubahan di tabel anggota / iuran.
     */
    @Transaction
    @Query("""
        SELECT
            a.anggota_id        AS anggota_id,
            a.nama_anggota      AS nama_anggota,
            i.iuran_id          AS iuran_id,
            i.nominal           AS nominal,
            i.status_bayar      AS status_bayar,
            i.terlambat         AS terlambat,
            i.metode_pembayaran AS metode_pembayaran,
            i.tanggal_bayar     AS tanggal_bayar,
            i.catatan           AS catatan
        FROM anggota a
        LEFT JOIN iuran i
            ON a.anggota_id = i.anggota_id
            AND i.bulan = :bulan AND i.tahun = :tahun
        ORDER BY a.nama_anggota ASC
    """)
    fun observeIuranByMonth(bulan: Int, tahun: Int): Flow<List<IuranAnggotaRow>>

    /** Sama seperti di atas tapi suspend (one-shot) — dipakai oleh laporan */
    @Transaction
    @Query("""
        SELECT * FROM iuran WHERE bulan = :bulan AND tahun = :tahun
    """)
    suspend fun getIuranByMonth(bulan: Int, tahun: Int): List<IuranEntity>

    /** Ringkasan iuran (untuk Home & IuranScreen header) */
    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN i.status_bayar = 1 THEN i.nominal ELSE 0 END), 0) AS totalTerkumpul,
            COUNT(CASE WHEN i.status_bayar = 1 THEN 1 END)                            AS lunasCount,
            COUNT(CASE WHEN i.status_bayar IS NULL OR i.status_bayar = 0 THEN 1 END)   AS belumBayarCount
        FROM anggota a
        LEFT JOIN iuran i
            ON a.anggota_id = i.anggota_id
            AND i.bulan = :bulan AND i.tahun = :tahun
    """)
    fun getIuranSummaryByMonth(bulan: Int, tahun: Int): Flow<IuranSummaryRaw?>

    /** Total nominal iuran yang sudah lunas pada bulan tertentu (untuk laporan iuran bulanan) */
    @Query("""
        SELECT COALESCE(SUM(nominal), 0) FROM iuran
        WHERE bulan = :bulan AND tahun = :tahun AND status_bayar = 1
    """)
    suspend fun getTotalIuranByMonth(bulan: Int, tahun: Int): Double?

    /** Jumlah anggota yang BELUM membayar pada bulan tertentu (untuk reminder worker) */
    @Query("""
        SELECT 
            (SELECT COUNT(*) FROM anggota) -
            (SELECT COUNT(*) FROM iuran WHERE bulan = :bulan AND tahun = :tahun AND status_bayar = 1)
    """)
    suspend fun countBelumBayar(bulan: Int, tahun: Int): Int

    @Query("SELECT * FROM iuran WHERE anggota_id = :anggotaId AND bulan = :bulan AND tahun = :tahun LIMIT 1")
    suspend fun getIuranByAnggotaAndMonth(anggotaId: Int, bulan: Int, tahun: Int): IuranEntity?

    @Query("DELETE FROM iuran WHERE iuran_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM iuran")
    suspend fun deleteAllIuran()
}