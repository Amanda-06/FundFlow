package com.example.fundflow.core.firebase

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.fundflow.core.datastore.SettingsDataStore // TAMBAHAN IMPORT DATASTORE
import com.example.fundflow.core.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first // TAMBAHAN IMPORT FLOW
import kotlinx.coroutines.runBlocking // TAMBAHAN IMPORT RUNBLOCKING
import javax.inject.Inject

/**
 * Service FCM untuk menerima notifikasi push dari Firebase Cloud Messaging.
 */
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var settingsDataStore: SettingsDataStore // TAMBAHAN: SUNTIKKAN SETTINGSDATASTORE

    companion object {
        private const val TAG = "FCMService"

        // Channel ID untuk notifikasi — harus sama dengan yang dibuat di NotificationHelper
        const val CHANNEL_ID_IURAN = "channel_iuran_reminder"
        const val CHANNEL_ID_GENERAL = "channel_general"
    }

    /**
     * Dipanggil saat token FCM diperbarui.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token baru: $token")
        // TODO: Simpan token ke Firestore users/{userId}/fcmToken jika dibutuhkan di backend massal
    }

    /**
     * Dipanggil saat notifikasi diterima dalam kondisi app foreground maupun background.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Pesan diterima dari: ${remoteMessage.from}")

        // ── PROTEKSI LOKAL: BACA STATUS TOGEL DARI DATASTORE ──
        val isNotificationEnabled = runBlocking {
            settingsDataStore.isNotificationEnabled.first()
        }

        // Jika user mematikan notifikasi di settings UI, block dan batalkan penayangan notif
        if (!isNotificationEnabled) {
            Log.d(TAG, "Notifikasi FCM diabaikan/diblokir karena toggle di Pengaturan berstatus OFF")
            return
        }
        // ──────────────────────────────────────────────────────

        // Ambil data dari notification payload jika lolos proteksi
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "FundFlow"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Kamu punya notifikasi baru"

        val channelId = remoteMessage.data["channel"] ?: CHANNEL_ID_GENERAL

        // Tampilkan notifikasi lokal
        notificationHelper.showNotification(
            title     = title,
            message   = body,
            channelId = channelId
        )
    }
}