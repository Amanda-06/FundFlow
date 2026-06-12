package com.example.fundflow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fundflow.feature.home.presentation.HomeScreen
import com.example.fundflow.feature.iuran.presentation.IuranScreen
import com.example.fundflow.feature.laporan.presentation.LaporanScreen
import com.example.fundflow.feature.pemasukan.presentation.PemasukanScreen
import com.example.fundflow.feature.pengeluaran.presentation.PengeluaranScreen
import com.example.fundflow.ui.theme.AppBackground

/**
 * Container utama setelah login — berisi Bottom Navigation Bar
 * dan NavHost terpisah (nested graph) untuk 5 tab:
 * Home, Iuran, Pemasukan, Pengeluaran, Laporan.
 *
 * Navigasi ke layar di luar bottom nav (Anggota, Profile, Settings, dll)
 * menggunakan [rootNavController] agar bottom bar otomatis hilang
 * (karena route tersebut berada di luar graph "main").
 *
 * @param rootNavController NavController dari AppNavGraph (top-level)
 */
@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    /**
     * Helper navigasi antar-tab — dipakai juga oleh BottomNavBar.
     * Disamakan di sini agar perilaku "Tagih" (Home -> Iuran)
     * konsisten dengan tap langsung di bottom nav (state tab lain
     * tetap tersimpan via saveState/restoreState).
     */
    fun navigateToTab(route: String) {
        bottomNavController.navigate(route) {
            popUpTo(bottomNavController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState    = true
        }
    }

    Scaffold(
        bottomBar = { FundFlowBottomNavBar(navController = bottomNavController) },
        containerColor = AppBackground
    ) { innerPadding ->
        // ── FIX TOP PADDING ──────────────────────────────────────
        // Hanya ambil padding BAWAH (untuk bottom nav bar) dari
        // outer Scaffold. Padding ATAS (status bar) TIDAK diambil
        // di sini karena setiap screen tab (Home via
        // .statusBarsPadding(), atau Iuran/Pemasukan/Pengeluaran/
        // Laporan via Scaffold+TopAppBar masing-masing) SUDAH
        // menangani inset status bar sendiri. Jika top padding
        // outer Scaffold ikut diterapkan di sini, hasilnya jarak
        // ke status bar jadi DOBEL (terlalu jauh ke bawah).
        NavHost(
            navController    = bottomNavController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ── Tab: Home ───────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProfile  = { rootNavController.navigate(Screen.Profile.route) },
                    onNavigateToSettings = { rootNavController.navigate(Screen.Settings.route) },
                    onNavigateToIuran    = { navigateToTab(Screen.Iuran.route) }
                )
            }

            // ── Tab: Iuran ──────────────────────────────────────
            composable(Screen.Iuran.route) {
                IuranScreen(
                    onNavigateToAnggota = { rootNavController.navigate(Screen.Anggota.route) }
                )
            }

            // ── Tab: Pemasukan ──────────────────────────────────
            composable(Screen.Pemasukan.route) {
                PemasukanScreen(
                    onNavigateBack = { /* Tab utama: tidak ada back, no-op */ }
                )
            }

            // ── Tab: Pengeluaran ─────────────────────────────────
            composable(Screen.Pengeluaran.route) {
                PengeluaranScreen(
                    onNavigateBack = { /* Tab utama: tidak ada back, no-op */ }
                )
            }

            // ── Tab: Laporan ─────────────────────────────────────
            composable(Screen.Laporan.route) {
                LaporanScreen()
            }
        }
    }
}