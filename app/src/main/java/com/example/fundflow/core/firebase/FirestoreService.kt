package com.example.fundflow.core.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper generic untuk operasi CRUD ke Firestore.
 * Dipakai oleh fitur yang membutuhkan cloud sync
 * (misalnya: Profile, Settings global, dll).
 *
 * Struktur koleksi Firestore:
 *   users/{userId}/profile
 *   users/{userId}/anggota/{anggotaId}
 *   users/{userId}/iuran/{iuranId}
 *   ... dst
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ── Helpers ───────────────────────────────────────────────

    /** Shorthand untuk mendapatkan document reference di bawah user tertentu */
    fun userDoc(userId: String) = firestore.collection("users").document(userId)

    /** Shorthand untuk sub-collection di bawah user */
    fun userCollection(userId: String, collection: String) =
        userDoc(userId).collection(collection)

    // ── Generic CRUD ─────────────────────────────────────────

    /**
     * Simpan / update sebuah dokumen.
     * [SetOptions.merge()] → hanya field yang dikirim yang akan diupdate.
     */
    suspend fun <T : Any> setDocument(
        userId: String,
        collection: String,
        documentId: String,
        data: T
    ) {
        userCollection(userId, collection)
            .document(documentId)
            .set(data, SetOptions.merge())
            .await()
    }

    /**
     * Baca satu dokumen dan kembalikan sebagai Map.
     * Return null jika dokumen tidak ada.
     */
    suspend fun getDocument(
        userId: String,
        collection: String,
        documentId: String
    ): Map<String, Any>? {
        val snapshot = userCollection(userId, collection)
            .document(documentId)
            .get()
            .await()
        return if (snapshot.exists()) snapshot.data else null
    }

    /**
     * Hapus dokumen dari Firestore.
     */
    suspend fun deleteDocument(
        userId: String,
        collection: String,
        documentId: String
    ) {
        userCollection(userId, collection)
            .document(documentId)
            .delete()
            .await()
    }

    /**
     * Baca seluruh dokumen dalam sub-collection sebagai List<Map>.
     */
    suspend fun getCollection(
        userId: String,
        collection: String
    ): List<Map<String, Any>> {
        val snapshot = userCollection(userId, collection).get().await()
        return snapshot.documents.mapNotNull { it.data }
    }

    /**
     * Observe perubahan dokumen secara realtime menggunakan Flow.
     * Emit setiap kali ada perubahan di sisi Firestore.
     */
    fun observeDocument(
        userId: String,
        collection: String,
        documentId: String
    ): Flow<Map<String, Any>?> = callbackFlow {
        val listener = userCollection(userId, collection)
            .document(documentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(if (snapshot?.exists() == true) snapshot.data else null)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Simpan data profil pengguna di: users/{userId}/profile
     * Menggunakan merge agar field lain tidak terhapus.
     */
    suspend fun saveProfile(userId: String, data: Map<String, Any>) {
        firestore.collection("users")
            .document(userId)
            .set(data, SetOptions.merge())
            .await()
    }

    /**
     * Baca data profil pengguna dari: users/{userId}
     */
    suspend fun getProfile(userId: String): Map<String, Any>? {
        val snapshot = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        return if (snapshot.exists()) snapshot.data else null
    }
}
