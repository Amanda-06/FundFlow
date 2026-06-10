package com.example.fundflow.core.firebase

import android.util.Log
import com.example.fundflow.core.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service FCM untuk menerima notifikasi push dari Firebase Cloud Messaging.
 *
 * Daftarkan di AndroidManifest.xml:
 *   <service
 *       android:name=".core.firebase.MyFirebaseMessagingService"
 *       android:exported="false">
 *       <intent-filter>
 *           <action android:name="com.google.firebase.MESSAGING_EVENT" />
 *       </intent-filter>
 *   </service>
 */
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    companion object {
        private const val TAG = "FCMService"

        // Channel ID untuk notifikasi — harus sama dengan yang dibuat di NotificationHelper
        const val CHANNEL_ID_IURAN = "channel_iuran_reminder"
        const val CHANNEL_ID_GENERAL = "channel_general"
    }

    /**
     * Dipanggil saat token FCM diperbarui.
     * Simpan token baru ke Firestore agar server bisa mengirim notifikasi tepat sasaran.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token baru: $token")
        // TODO: Simpan token ke Firestore users/{userId}/fcmToken
        // Bisa dilakukan via FirestoreService yang di-inject
    }

    /**
     * Dipanggil saat notifikasi diterima dalam kondisi app foreground.
     * Untuk background / killed, sistem Android menangani sendiri.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Pesan diterima dari: ${remoteMessage.from}")

        // Ambil data dari notification payload
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
