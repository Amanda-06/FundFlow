package com.example.fundflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Silakan import Screen & ViewModel masing-masing fitur setelah file presentasi dibuat.
// Contoh: import com.example.fundflow.feature.home.presentation.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    // startDestination diatur ke Onboarding terlebih dahulu [cite: 81]
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            // OnboardingScreen(navController = navController) [cite: 16]
        }
        composable(Screen.Login.route) {
            // LoginScreen(navController = navController) [cite: 22]
        }
        composable(Screen.RegisterStep1.route) {
            // RegisterStep1Screen(navController = navController) [cite: 22]
        }
        composable(Screen.RegisterStep2.route) {
            // RegisterStep2Screen(navController = navController) [cite: 23]
        }
        composable(Screen.Home.route) {
            // HomeScreen(navController = navController) [cite: 28]
        }
        composable(Screen.Iuran.route) {
            // IuranScreen(navController = navController) [cite: 38]
        }
        composable(Screen.Anggota.route) {
            // AnggotaScreen(navController = navController) [cite: 32]
        }
        composable(Screen.Pemasukan.route) {
            // PemasukanScreen(navController = navController) [cite: 42]
        }
        composable(Screen.Pengeluaran.route) {
            // PengeluaranScreen(navController = navController) [cite: 47]
        }
        composable(Screen.Laporan.route) {
            // LaporanScreen(navController = navController) [cite: 52]
        }
        composable(Screen.Profile.route) {
            // ProfileScreen(navController = navController) [cite: 57]
        }
        composable(Screen.EditProfile.route) {
            // EditProfileScreen(navController = navController) [cite: 57]
        }
        composable(Screen.TentangFundFlow.route) {
            // TentangFundFlowScreen(navController = navController) [cite: 57]
        }
        composable(Screen.PusatBantuan.route) {
            // PusatBantuanScreen(navController = navController) [cite: 57]
        }
        composable(Screen.Settings.route) {
            // SettingsScreen(navController = navController) [cite: 61]
        }
        composable(Screen.PeriodeKas.route) {
            // PeriodeKasScreen(navController = navController) [cite: 62]
        }
    }
}