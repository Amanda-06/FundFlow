package com.example.fundflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fundflow.R
import com.example.fundflow.ui.theme.*

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

private fun labelResForRoute(route: String): Int = when (route) {
    Screen.Home.route        -> R.string.nav_home
    Screen.Iuran.route       -> R.string.nav_iuran
    Screen.Pemasukan.route   -> R.string.nav_pemasukan
    Screen.Pengeluaran.route -> R.string.nav_pengeluaran
    Screen.Laporan.route     -> R.string.nav_laporan
    else                     -> R.string.nav_home
}

@Composable
fun FundFlowBottomNavBar(navController: NavHostController) {
    val navBackStackEntry    by navController.currentBackStackEntryAsState()
    val currentDestination   = navBackStackEntry?.destination

    NavigationBar(
        containerColor = CardWhite,
        tonalElevation  = 4.dp
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            val resolvedLabel = stringResource(labelResForRoute(item.route))

            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    navController.navigate(item.route) {
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
                        contentDescription = resolvedLabel
                    )
                },
                label  = { Text(resolvedLabel) },
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