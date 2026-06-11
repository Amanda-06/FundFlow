package com.example.fundflow.navigation

/**
 * Sealed class untuk mendefinisikan rute string navigasi secara terpusat.
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object RegisterStep1 : Screen("register_step1")
    object RegisterStep2 : Screen("register_step2")
    object Home : Screen("home")
    object Iuran : Screen("iuran")
    object Anggota : Screen("anggota") // Dibuka dari FAB halaman Iuran [cite: 84]
    object Pemasukan : Screen("pemasukan")
    object Pengeluaran : Screen("pengeluaran")
    object Laporan : Screen("laporan")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object TentangFundFlow : Screen("tentang_fundflow")
    object PusatBantuan : Screen("pusat_bantuan")
    object Settings : Screen("settings")
    object PeriodeKas : Screen("periode_kas")
}