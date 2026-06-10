// ============================================================
// core/firebase/FirebaseAuthService.kt
// ============================================================
package com.example.fundflow.core.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    /** User Firebase yang sedang login, atau null jika belum login */
    val currentUser: FirebaseUser? get() = auth.currentUser

    val isLoggedIn: Boolean get() = auth.currentUser != null

    /**
     * Register pengguna baru dengan email + password.
     * Setelah berhasil, langsung update displayName.
     * @return FirebaseUser yang baru dibuat
     */
    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user   = result.user ?: error("Register berhasil tapi user null")

        // Update displayName di Firebase Auth profile
        val profileUpdates = userProfileChangeRequest {
            this.displayName = displayName
        }
        user.updateProfile(profileUpdates).await()
        return user
    }

    /**
     * Login dengan email atau username.
     * Firebase Auth hanya mendukung email, jadi pastikan email yang dikirim.
     * @return FirebaseUser yang berhasil login
     */
    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: error("Login berhasil tapi user null")
    }

    /** Logout dari Firebase Auth */
    fun logout() {
        auth.signOut()
    }

    /**
     * Kirim email reset password ke alamat yang diberikan.
     */
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Perbarui email pengguna yang sedang login.
     */
    suspend fun updateEmail(newEmail: String) {
        currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
            ?: error("Tidak ada user yang sedang login")
    }

    /**
     * Perbarui password pengguna yang sedang login.
     */
    suspend fun updatePassword(newPassword: String) {
        currentUser?.updatePassword(newPassword)?.await()
            ?: error("Tidak ada user yang sedang login")
    }
}
