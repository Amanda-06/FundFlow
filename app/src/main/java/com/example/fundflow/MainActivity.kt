package com.example.fundflow

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fundflow.core.datastore.SettingsDataStore
import com.example.fundflow.appNavigation.AppNavGraph
import com.example.fundflow.navigation.BottomNavBar
import com.example.fundflow.navigation.Screen
import com.example.fundflow.ui.theme.FundFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

/**
 * Single Activity Utama untuk FundFlow.
 * Sudah menggabungkan manajemen Tema (DataStore), Bahasa (Locale), Window Edge-to-Edge,
 * serta Kontrol Visibilitas Bottom Navigation Bar secara aman.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun attachBaseContext(newBase: Context) {
        // [FITUR BAHASA] Mengambil preferensi bahasa secara sinkronus via SharedPreferences 
        // sebelum activity ter-attach demi kelancaran i18n string resource di awal startup.
        val lang = newBase.getSharedPreferences("fundflow_lang", Context.MODE_PRIVATE)
            .getString("language", "id") ?: "id"
        super.attachBaseContext(applyLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [FITUR TAMPILAN] Mengaktifkan tampilan penuh menembus status bar & navigation bar bawaan HP
        enableEdgeToEdge()

        setContent {
            // [FITUR TEMA] Mengamati preferensi tema dari DataStore secara lifecycle-aware
            val isDarkTheme by settingsDataStore.isDarkTheme
                .collectAsStateWithLifecycle(initialValue = false)

            FundFlowTheme(darkTheme = isDarkTheme) {
                // Inisialisasi NavController di level Activity agar bisa di-share ke Scaffold & AppNavGraph
                val navController = rememberNavController()

                // POIN 3: Memantau state navigasi secara real-time
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // POIN 2: Menentukan halaman mana saja yang berhak menampilkan Bottom Bar
                val bottomBarScreens = listOf(
                    Screen.Home.route,
                    Screen.Iuran.route,
                    Screen.Pemasukan.route,
                    Screen.Pengeluaran.route,
                    Screen.Laporan.route
                )
                val showBottomBar = currentRoute in bottomBarScreens

                // Struktur Layout Utama Material 3
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    // POIN 1: Manajemen Padding (innerPadding) agar konten tidak tertutup oleh BottomBar
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // <--- Mengonsumsi padding Scaffold di sini
                    ) {
                        // Memanggil Grafik Navigasi dan mengoper navController yang sudah di-hoist
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Fungsi utilitas untuk menerapkan konfigurasi bahasa kustom ke Context.
         * Dipanggil dari attachBaseContext dan SettingsScreen (diikuti recreate()).
         */
        fun applyLocale(context: Context, languageCode: String): Context {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration).apply {
                setLocale(locale)
            }
            return context.createConfigurationContext(config)
        }
    }
}