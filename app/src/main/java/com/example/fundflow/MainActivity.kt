package com.example.fundflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fundflow.appNavigation.AppNavGraph
import com.example.fundflow.navigation.BottomNavBar
import com.example.fundflow.navigation.Screen
import com.example.fundflow.ui.theme.FundFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // FundFlowTheme mendukung dynamic dark/light mode [cite: 13]
            FundFlowTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Menentukan screen mana saja yang menampilkan Bottom Navigation Bar [cite: 82]
                val bottomBarScreens = listOf(
                    Screen.Home.route,
                    Screen.Iuran.route,
                    Screen.Pemasukan.route,
                    Screen.Pengeluaran.route,
                    Screen.Laporan.route
                )
                val showBottomBar = currentRoute in bottomBarScreens

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Memanggil grafik navigasi utama aplikasi
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}