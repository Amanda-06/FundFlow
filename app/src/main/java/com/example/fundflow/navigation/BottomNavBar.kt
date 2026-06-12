// ============================================================
// navigation/BottomNavBar.kt  (FIXED)
// ============================================================
package com.example.fundflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fundflow.ui.theme.*

/**
 * Daftar item bottom navigation sesuai 5 tab utama FundFlow.
 * Setiap item punya warna aktif sendiri sesuai tema semantik
 * masing-masing fitur (Iuran=biru, Pemasukan=hijau, dst).
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        route          = Screen.Home.route,
        label          = "Home",
        selectedIcon   = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        activeColor    = GrayMedium
    ),
    BottomNavItem(
        route          = Screen.Iuran.route,
        label          = "Iuran",
        selectedIcon   = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group,
        activeColor    = IuranBlue
    ),
    BottomNavItem(
        route          = Screen.Pemasukan.route,
        label          = "Masuk",
        selectedIcon   = Icons.Filled.TrendingUp,
        unselectedIcon = Icons.Outlined.TrendingUp,
        activeColor    = IncomeGreen
    ),
    BottomNavItem(
        route          = Screen.Pengeluaran.route,
        label          = "Keluar",
        selectedIcon   = Icons.Filled.TrendingDown,
        unselectedIcon = Icons.Outlined.TrendingDown,
        activeColor    = ExpenseRed
    ),
    BottomNavItem(
        route          = Screen.Laporan.route,
        label          = "Laporan",
        selectedIcon   = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment,
        activeColor    = ReportOrange
    )
)

/**
 * Bottom Navigation Bar FundFlow.
 *
 * Perbaikan dari versi sebelumnya:
 * - Background PUTIH (CardWhite), bukan gelap (NavBackground)
 * - Setiap tab punya warna aktif sendiri (IuranBlue, IncomeGreen, ExpenseRed, ReportOrange)
 * - Tab tidak aktif berwarna abu-abu (GrayMedium)
 * - Label dipersingkat ("Masuk"/"Keluar") agar tidak wrap ke 2 baris
 * - TIDAK ada `Modifier.height(64.dp)` fixed — biarkan NavigationBar
 *   menghitung tinggi secara otomatis (termasuk inset gesture bar),
 *   sehingga tidak ada konten yang terpotong di bawah.
 *
 * @param navController NavController untuk graph bottom-nav (tabs only).
 */
@Composable
fun FundFlowBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = CardWhite,
        tonalElevation  = 4.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    navController.navigate(item.route) {
                        // Hindari menumpuk banyak salinan tab yang sama di back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon = {
                    Icon(
                        imageVector        = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = item.activeColor,
                    selectedTextColor   = item.activeColor,
                    unselectedIconColor = GrayMedium,
                    unselectedTextColor = GrayMedium,
                    indicatorColor      = item.activeColor.copy(alpha = 0.14f)
                )
            )
        }
    }
}