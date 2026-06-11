// ============================================================
// MainActivity.kt
// ============================================================
package com.example.fundflow

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.navigation.AppNavGraph
import com.example.fundflow.ui.theme.FundFlowTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

/**
 * EntryPoint untuk mengakses singleton [SettingsDataStore] dari
 * attachBaseContext(), di mana injeksi Hilt standar (@Inject field)
 * belum tersedia karena super.onCreate() belum dipanggil.
 *
 * Menggunakan EntryPointAccessors memastikan kita mendapatkan
 * INSTANCE SINGLETON yang sama dengan yang di-provide AppModule —
 * bukan instance baru (yang akan menyebabkan crash "multiple
 * DataStores active for the same file").
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsDataStoreEntryPoint {
    fun settingsDataStore(): SettingsDataStore
}

/**
 * Single Activity untuk seluruh aplikasi FundFlow.
 *
 * Tanggung jawab:
 * 1. Menerapkan locale (bahasa ID/EN) sebelum Activity ter-attach,
 *    dibaca dari singleton [SettingsDataStore] via EntryPointAccessors.
 * 2. Membaca preferensi tema (dark/light) secara reaktif dan
 *    meneruskannya ke [FundFlowTheme].
 * 3. Menyediakan fungsi [recreateActivity] yang dipanggil dari
 *    SettingsScreen setelah pengguna mengganti bahasa, agar seluruh
 *    string resource ter-refresh sesuai locale baru.
 * 4. Memanggil [AppNavGraph] sebagai entry point navigasi.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun attachBaseContext(newBase: Context) {
        // Ambil instance SINGLETON SettingsDataStore via Hilt EntryPoint
        // (aman dipanggil sebelum proses injeksi @AndroidEntryPoint selesai).
        val savedLanguage = runCatching {
            val entryPoint = EntryPointAccessors.fromApplication(
                newBase.applicationContext,
                SettingsDataStoreEntryPoint::class.java
            )
            runBlocking { entryPoint.settingsDataStore().language.first() }
        }.getOrDefault("id")

        super.attachBaseContext(applyLocale(newBase, savedLanguage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observe preferensi tema secara reaktif dari DataStore
            val isDarkTheme by settingsDataStore.isDarkTheme
                .collectAsStateWithLifecycle(initialValue = false)

            FundFlowTheme(darkTheme = isDarkTheme) {
                AppNavGraph(
                    onLanguageChanged = { recreateActivity() }
                )
            }
        }
    }

    /**
     * Recreate Activity — dipanggil setelah bahasa diganti di SettingsScreen
     * agar attachBaseContext() dipanggil ulang dengan locale baru,
     * sehingga seluruh teks UI ter-refresh ke bahasa yang baru dipilih.
     */
    private fun recreateActivity() {
        recreate()
    }

    companion object {
        /**
         * Terapkan [languageCode] ("id" | "en") ke [context] dan
         * kembalikan context baru dengan konfigurasi locale yang sudah diperbarui.
         */
        fun applyLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = Configuration(context.resources.configuration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
                config.setLayoutDirection(locale)
            } else {
                @Suppress("DEPRECATION")
                config.locale = locale
            }
            return context.createConfigurationContext(config)
        }
    }
}