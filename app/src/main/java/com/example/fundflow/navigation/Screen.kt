// ============================================================
// navigation/Screen.kt
// ============================================================
package com.example.fundflow.navigation

/**
 * Daftar seluruh route navigasi dalam aplikasi FundFlow.
 * Dikelompokkan berdasarkan alur: Auth, Main (Bottom Nav), dan Detail/Sub screens.
 */
sealed class Screen(val route: String) {

    // ── Auth Flow ─────────────────────────────────────────────
    data object Onboarding   : Screen("onboarding")
    data object Login        : Screen("login")
    data object RegisterStep1 : Screen("register_step1")
    data object RegisterStep2 : Screen("register_step2")

    // ── Main Container (berisi Bottom Navigation) ─────────────
    data object Main : Screen("main")

    // ── Bottom Navigation Tabs ─────────────────────────────────
    data object Home        : Screen("home")
    data object Iuran       : Screen("iuran")
    data object Pemasukan   : Screen("pemasukan")
    data object Pengeluaran : Screen("pengeluaran")
    data object Laporan     : Screen("laporan")

    // ── Sub / Detail Screens (top-level, di luar bottom nav) ──
    data object Anggota          : Screen("anggota")
    data object Profile          : Screen("profile")
    data object EditProfile      : Screen("edit_profile")
    data object TentangFundFlow  : Screen("tentang_fundflow")
    data object PusatBantuan     : Screen("pusat_bantuan")
    data object Settings         : Screen("settings")
    data object PengaturanPeriode : Screen("pengaturan_periode")
}

/**
 * Item untuk Bottom Navigation Bar.
 * Memetakan route ke label dan ikon yang sesuai.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)