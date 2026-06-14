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

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

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