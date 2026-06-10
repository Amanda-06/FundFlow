package com.example.fundflow.core.di

import android.content.Context
import androidx.room.Room
import com.example.fundflow.core.database.FundFlowDatabase
import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.core.util.NotificationHelper
import com.example.fundflow.core.util.StringResProvider
import com.example.fundflow.core.util.StringResProviderImpl
import com.example.fundflow.feature.anggota.data.local.AnggotaDao
import com.example.fundflow.feature.auth.data.local.UserDao
import com.example.fundflow.feature.iuran.data.local.IuranDao
import com.example.fundflow.feature.iuran.data.local.PeriodeDao
import com.example.fundflow.feature.laporan.data.local.LaporanDao
import com.example.fundflow.feature.pemasukan.data.local.PemasukanDao
import com.example.fundflow.feature.pengeluaran.data.local.PengeluaranDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Room Database ─────────────────────────────────────────
    @Provides
    @Singleton
    fun provideFundFlowDatabase(
        @ApplicationContext context: Context
    ): FundFlowDatabase = Room.databaseBuilder(
        context,
        FundFlowDatabase::class.java,
        "fundflow.db"
    )
        .fallbackToDestructiveMigration()   // Ganti ke Migration saat production
        .build()

    // ── DAOs ──────────────────────────────────────────────────
    @Provides
    fun provideUserDao(db: FundFlowDatabase): UserDao = db.userDao()

    @Provides
    fun provideAnggotaDao(db: FundFlowDatabase): AnggotaDao = db.anggotaDao()

    @Provides
    fun providePeriodeDao(db: FundFlowDatabase): PeriodeDao = db.periodeDao()

    @Provides
    fun provideIuranDao(db: FundFlowDatabase): IuranDao = db.iuranDao()

    @Provides
    fun providePemasukanDao(db: FundFlowDatabase): PemasukanDao = db.pemasukanDao()

    @Provides
    fun providePengeluaranDao(db: FundFlowDatabase): PengeluaranDao = db.pengeluaranDao()

    @Provides
    fun provideLaporanDao(db: FundFlowDatabase): LaporanDao = db.laporanDao()

    // ── DataStore ─────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore = SettingsDataStore(context)

    // ── NotificationHelper ────────────────────────────────────
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): NotificationHelper = NotificationHelper(context)
}

// Binds menggunakan @Module terpisah (abstract class) agar Hilt bisa optimasi
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds
    @Singleton
    abstract fun bindStringResProvider(
        impl: StringResProviderImpl
    ): StringResProvider
}
