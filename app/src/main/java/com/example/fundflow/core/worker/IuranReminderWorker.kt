package com.example.fundflow.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fundflow.core.util.NotificationHelper
import com.example.fundflow.feature.iuran.data.local.IuranDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

/**
 * CoroutineWorker yang dijadwalkan via WorkManager.
 * Tugas: cek anggota yang belum bayar iuran di bulan berjalan,
 * lalu tampilkan notifikasi pengingat.
 *
 * Cara jadwalkan (contoh di MainActivity / Application):
 *
 *   val request = PeriodicWorkRequestBuilder<IuranReminderWorker>(1, TimeUnit.DAYS)
 *       .setConstraints(Constraints.Builder()
 *           .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
 *           .build())
 *       .build()
 *
 *   WorkManager.getInstance(context).enqueueUniquePeriodicWork(
 *       "iuran_reminder",
 *       ExistingPeriodicWorkPolicy.KEEP,
 *       request
 *   )
 */
@HiltWorker
class IuranReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val iuranDao: IuranDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val calendar     = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1   // Calendar.MONTH is 0-based
            val currentYear  = calendar.get(Calendar.YEAR)

            // Hitung berapa anggota yang belum bayar bulan ini
            val belumBayarCount = iuranDao.countBelumBayar(
                bulan = currentMonth,
                tahun = currentYear
            )

            if (belumBayarCount > 0) {
                notificationHelper.showNotification(
                    title     = "Pengingat Iuran",
                    message   = "Ada $belumBayarCount anggota yang belum membayar iuran bulan ini.",
                    channelId = NotificationHelper.CHANNEL_ID_IURAN
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
