// ============================================================
// navigation/BottomNavBar.kt
// ============================================================
package com.example.fundflow.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fundflow.ui.theme.NavBackground
import com.example.fundflow.ui.theme.NavIconActive
import com.example.fundflow.ui.theme.NavIconInactive

/**
 * Daftar item bottom navigation sesuai 5 tab utama FundFlow:
 * Home, Iuran, Pemasukan, Pengeluaran, Laporan.
 */
private val bottomNavItems = listOf(
    BottomNavItem(
        route          = Screen.Home.route,
        label          = "Home",
        selectedIcon   = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route          = Screen.Iuran.route,
        label          = "Iuran",
        selectedIcon   = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group
    ),
    BottomNavItem(
        route          = Screen.Pemasukan.route,
        label          = "Pemasukan",
        selectedIcon   = Icons.Filled.TrendingUp,
        unselectedIcon = Icons.Outlined.TrendingUp
    ),
    BottomNavItem(
        route          = Screen.Pengeluaran.route,
        label          = "Pengeluaran",
        selectedIcon   = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart
    ),
    BottomNavItem(
        route          = Screen.Laporan.route,
        label          = "Laporan",
        selectedIcon   = Icons.Filled.Assessment,
        unselectedIcon = Icons.Outlined.Assessment
    )
)

/**
 * Bottom Navigation Bar FundFlow.
 * Background gelap (NavBackground) sesuai desain, dengan ikon
 * lime (NavIconActive) saat tab aktif dan abu-abu (NavIconInactive) saat tidak aktif.
 *
 * @param navController NavController untuk graph bottom-nav (tabs only).
 */
@Composable
fun FundFlowBottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = NavBackground,
        modifier        = Modifier.height(64.dp)
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
                    selectedIconColor   = NavIconActive,
                    selectedTextColor   = NavIconActive,
                    unselectedIconColor = NavIconInactive,
                    unselectedTextColor = NavIconInactive,
                    indicatorColor      = NavBackground
                )
            )
        }
    }
}