package com.example.fundflow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fundflow.core.worker.IuranReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Application class FundFlow.
 *
 * Tanggung jawab:
 * 1. @HiltAndroidApp -> entry point dependency injection untuk seluruh app.
 * 2. Configuration.Provider -> menyediakan HiltWorkerFactory agar
 *    [IuranReminderWorker] (yang menggunakan @HiltWorker) bisa
 *    di-inject dependency-nya oleh Hilt saat dijalankan WorkManager.
 * 3. Menjadwalkan periodic worker untuk reminder iuran setiap hari.
 *
 * Daftarkan di AndroidManifest.xml:
 *   <application
 *       android:name=".FundFlowApp"
 *       ...>
 *
 * PENTING: karena WorkManager dikonfigurasi secara manual via
 * Configuration.Provider, nonaktifkan default initializer dengan
 * menambahkan di AndroidManifest.xml:
 *
 *   <provider
 *       android:name="androidx.startup.InitializationProvider"
 *       android:authorities="${applicationId}.androidx-startup"
 *       android:exported="false"
 *       tools:node="merge">
 *       <meta-data
 *           android:name="androidx.work.WorkManagerInitializer"
 *           android:value="androidx.startup.InitializationProvider"
 *           tools:node="remove" />
 *   </provider>
 */
@HiltAndroidApp
class FundFlowApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleIuranReminder()
    }

    /**
     * Jadwalkan [IuranReminderWorker] berjalan setiap 24 jam.
     * Menggunakan KEEP agar tidak membuat duplikat job jika app
     * di-restart berkali-kali.
     */
    private fun scheduleIuranReminder() {
        val request = PeriodicWorkRequestBuilder<IuranReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "iuran_reminder_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}