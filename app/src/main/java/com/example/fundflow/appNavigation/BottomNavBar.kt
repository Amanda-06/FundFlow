package com.example.fundflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Representasi item menu yang tampil pada Bottom Navigation Bar[cite: 6, 82].
 */
sealed class BottomNavItem(val screen: Screen, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home, "Home", Icons.Default.Home)
    object Iuran : BottomNavItem(Screen.Iuran, "Iuran", Icons.Default.DateRange)
    object Pemasukan : BottomNavItem(Screen.Pemasukan, "Pemasukan", Icons.Default.List)
    object Pengeluaran : BottomNavItem(Screen.Pengeluaran, "Pengeluaran", Icons.Default.Menu)
    object Laporan : BottomNavItem(Screen.Laporan, "Laporan", Icons.Default.AccountCircle)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Iuran,
        BottomNavItem.Pemasukan,
        BottomNavItem.Pengeluaran,
        BottomNavItem.Laporan
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            // Menghindari penumpukan backstack secara berlebihan
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}