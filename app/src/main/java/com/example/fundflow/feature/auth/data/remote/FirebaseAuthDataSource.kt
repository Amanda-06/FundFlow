package com.example.fundflow.feature.auth.data.remote

import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.firebase.FirestoreService
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

/**
 * Bertanggung jawab untuk semua komunikasi dengan Firebase Authentication
 * dan penyimpanan data user ke Firestore.
 * Dipanggil oleh AuthRepositoryImpl — tidak langsung dari ViewModel.
 */
class FirebaseAuthDataSource @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) {
    val currentFirebaseUser: FirebaseUser? get() = authService.currentUser
    val isLoggedIn: Boolean get() = authService.isLoggedIn

    /** Register baru: buat akun Firebase Auth + simpan profil ke Firestore */
    suspend fun register(
        email: String,
        password: String,
        namaLengkap: String,
        username: String,
        namaOrganisasi: String
    ): FirebaseUser {
        val user = authService.register(email, password, namaLengkap)
        // Simpan data tambahan ke Firestore
        firestoreService.saveProfile(
            userId = user.uid,
            data   = mapOf(
                "user_id"         to user.uid,
                "nama_lengkap"    to namaLengkap,
                "username"        to username,
                "email"           to email,
                "nama_organisasi" to namaOrganisasi,
                "created_at"      to System.currentTimeMillis()
            )
        )
        return user
    }

    /** Login dengan email + password */
    suspend fun login(email: String, password: String): FirebaseUser =
        authService.login(email, password)

    /** Logout dari Firebase */
    fun logout() = authService.logout()

    /** Kirim email reset password */
    suspend fun sendPasswordReset(email: String) =
        authService.sendPasswordResetEmail(email)

    /** Ambil profil dari Firestore untuk disimpan ke Room lokal */
    suspend fun fetchProfile(userId: String): Map<String, Any>? =
        firestoreService.getProfile(userId)
}