package com.example.fundflow.feature.iuran.data.local

import androidx.room.*
import com.example.fundflow.feature.iuran.data.model.PeriodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriode(periode: PeriodeEntity): Long

    @Update
    suspend fun updatePeriode(periode: PeriodeEntity)

    /** Setiap user punya 1 periode aktif (relasi 1:1) */
    @Query("SELECT * FROM periode WHERE user_id = :userId LIMIT 1")
    suspend fun getPeriodeByUserId(userId: String): PeriodeEntity?

    @Query("SELECT * FROM periode WHERE user_id = :userId LIMIT 1")
    fun observePeriode(userId: String): Flow<PeriodeEntity?>

    @Query("DELETE FROM periode WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String)
}