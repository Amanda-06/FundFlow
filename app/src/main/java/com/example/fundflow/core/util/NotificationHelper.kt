// ============================================================
// core/util/NotificationHelper.kt
// ============================================================
package com.example.fundflow.core.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fundflow.R
import com.example.fundflow.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper untuk membuat notification channel dan menampilkan notifikasi lokal.
 * Dipakai oleh IuranReminderWorker dan MyFirebaseMessagingService.
 *
 * Channel yang tersedia:
 *   - CHANNEL_ID_IURAN    → Pengingat iuran bulanan
 *   - CHANNEL_ID_GENERAL  → Notifikasi umum FundFlow
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID_IURAN    = "channel_iuran_reminder"
        const val CHANNEL_ID_GENERAL  = "channel_general"
        private var notifIdCounter    = 1000
    }

    init {
        createNotificationChannels()
    }

    /** Buat semua channel saat helper di-inject (init dipanggil sekali) */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: Iuran Reminder
            NotificationChannel(
                CHANNEL_ID_IURAN,
                "Pengingat Iuran",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi pengingat pembayaran iuran bulanan"
                manager.createNotificationChannel(this)
            }

            // Channel 2: General
            NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Notifikasi Umum",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi umum dari FundFlow"
                manager.createNotificationChannel(this)
            }
        }
    }

    /**
     * Tampilkan notifikasi.
     * @param title     Judul notifikasi
     * @param message   Isi notifikasi
     * @param channelId ID channel (default: CHANNEL_ID_GENERAL)
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        title: String,
        message: String,
        channelId: String = CHANNEL_ID_GENERAL
    ) {
        // PendingIntent — buka MainActivity saat notif diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_fundflow_notif)   // pastikan drawable ini ada di res/drawable
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notifIdCounter++, notification)
    }

    /** Batalkan semua notifikasi aktif */
    fun cancelAll() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
