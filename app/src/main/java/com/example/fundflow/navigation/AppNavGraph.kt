package com.example.fundflow.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.fundflow.feature.anggota.presentation.AnggotaScreen
import com.example.fundflow.feature.auth.presentation.login.LoginScreen
import com.example.fundflow.feature.auth.presentation.register.RegisterStep1Screen
import com.example.fundflow.feature.auth.presentation.register.RegisterStep2Screen
import com.example.fundflow.feature.auth.presentation.register.RegisterViewModel
import com.example.fundflow.feature.onboarding.presentation.OnboardingScreen
import com.example.fundflow.feature.profile.presentation.EditProfilScreen
import com.example.fundflow.feature.profile.presentation.ProfileScreen
import com.example.fundflow.feature.profile.presentation.PusatBantuanScreen
import com.example.fundflow.feature.profile.presentation.TentangFundFlowScreen
import com.example.fundflow.feature.settings.presentation.PengaturanPeriodeScreen
import com.example.fundflow.feature.settings.presentation.SettingsScreen
import com.example.fundflow.ui.theme.PrimaryLime


private const val REGISTER_GRAPH_ROUTE = "register_graph"

@Composable
fun AppNavGraph(
    onLanguageChanged: () -> Unit,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val navController: NavHostController = rememberNavController()
    val startState by appViewModel.startState.collectAsStateWithLifecycle()

    // Tampilkan loading singkat selagi membaca preferensi DataStore
    if (startState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryLime)
        }
        return
    }

    val startDestination = when {
        !startState.hasSeenOnboarding -> Screen.Onboarding.route
        !startState.isLoggedIn        -> Screen.Login.route
        else                           -> Screen.Main.route
    }

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(REGISTER_GRAPH_ROUTE)
                }
            )
        }

        navigation(
            startDestination = Screen.RegisterStep1.route,
            route            = REGISTER_GRAPH_ROUTE
        ) {
            composable(Screen.RegisterStep1.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(REGISTER_GRAPH_ROUTE)
                }
                val sharedViewModel: RegisterViewModel = hiltViewModel(parentEntry)

                RegisterStep1Screen(
                    onNavigateToStep2 = {
                        navController.navigate(Screen.RegisterStep2.route)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(REGISTER_GRAPH_ROUTE) { inclusive = true }
                        }
                    },
                    viewModel = sharedViewModel
                )
            }

            composable(Screen.RegisterStep2.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(REGISTER_GRAPH_ROUTE)
                }
                val sharedViewModel: RegisterViewModel = hiltViewModel(parentEntry)

                RegisterStep2Screen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Main.route) {
                            // Bersihkan seluruh back stack auth flow (termasuk register_graph)
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    viewModel = sharedViewModel
                )
            }
        }

        composable(Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }

        composable(Screen.Anggota.route) {
            AnggotaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack          = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToAbout       = { navController.navigate(Screen.TentangFundFlow.route) },
                onNavigateToHelp        = { navController.navigate(Screen.PusatBantuan.route) },
                onLoggedOut = {
                    // Bersihkan seluruh back stack -> kembali ke Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfilScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TentangFundFlow.route) {
            TentangFundFlowScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PusatBantuan.route) {
            PusatBantuanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack      = { navController.popBackStack() },
                onNavigateToPeriode = { navController.navigate(Screen.PengaturanPeriode.route) },
                onLanguageChanged   = onLanguageChanged
            )
        }

        composable(Screen.PengaturanPeriode.route) {
            PengaturanPeriodeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}