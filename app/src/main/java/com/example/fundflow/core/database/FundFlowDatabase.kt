package com.example.fundflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.anggota.data.local.AnggotaEntity
import com.example.fundflow.feature.auth.data.local.UserDao
import com.example.fundflow.feature.auth.data.model.UserEntity
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.iuran.data.model.IuranEntity
import com.example.fundflow.feature.iuran.data.model.PeriodeEntity
import com.example.fundflow.feature.laporan.data.local.LaporanDao
import com.example.fundflow.feature.laporan.data.model.LaporanEntity
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pemasukan.data.model.PemasukanEntity
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import com.example.fundflow.feature.pengeluaran.data.model.PengeluaranEntity

@Database(
    entities = [
        UserEntity::class,
        AnggotaEntity::class,
        PeriodeEntity::class,
        IuranEntity::class,
        PemasukanEntity::class,
        PengeluaranEntity::class,
        LaporanEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FundFlowDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun anggotaDao(): AnggotaDao
    abstract fun periodeDao(): PeriodeDao
    abstract fun iuranDao(): IuranDao
    abstract fun pemasukanDao(): PemasukanDao
    abstract fun pengeluaranDao(): PengeluaranDao
    abstract fun laporanDao(): LaporanDao
}
