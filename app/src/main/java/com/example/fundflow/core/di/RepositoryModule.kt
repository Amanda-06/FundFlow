package com.example.fundflow.core.di

import com.example.fundflow.feature.anggota.data.repository.AnggotaRepositoryImpl
import com.example.fundflow.feature.anggota.domain.repository.AnggotaRepository
import com.example.fundflow.feature.auth.data.repository.AuthRepositoryImpl
import com.example.fundflow.feature.auth.domain.repository.AuthRepository
import com.example.fundflow.feature.home.data.repository.HomeRepositoryImpl
import com.example.fundflow.feature.home.domain.repository.HomeRepository
import com.example.fundflow.feature.iuran.data.repository.IuranRepositoryImpl
import com.example.fundflow.feature.iuran.domain.repository.IuranRepository
import com.example.fundflow.feature.laporan.data.repository.LaporanRepositoryImpl
import com.example.fundflow.feature.laporan.domain.repository.LaporanRepository
import com.example.fundflow.feature.pemasukan.data.repository.PemasukanRepositoryImpl
import com.example.fundflow.feature.pemasukan.domain.repository.PemasukanRepository
import com.example.fundflow.feature.pengeluaran.data.repository.PengeluaranRepositoryImpl
import com.example.fundflow.feature.pengeluaran.domain.repository.PengeluaranRepository
import com.example.fundflow.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.fundflow.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds @Singleton
    abstract fun bindAnggotaRepository(impl: AnggotaRepositoryImpl): AnggotaRepository

    @Binds @Singleton
    abstract fun bindIuranRepository(impl: IuranRepositoryImpl): IuranRepository

    @Binds @Singleton
    abstract fun bindPemasukanRepository(impl: PemasukanRepositoryImpl): PemasukanRepository

    @Binds @Singleton
    abstract fun bindPengeluaranRepository(impl: PengeluaranRepositoryImpl): PengeluaranRepository

    @Binds @Singleton
    abstract fun bindLaporanRepository(impl: LaporanRepositoryImpl): LaporanRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
