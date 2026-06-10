// ============================================================
// feature/auth/presentation/login/LoginScreen.kt
// ============================================================
package com.example.fundflow.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigasi setelah login berhasil
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccessState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundKrem)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))

            // ── Logo + Nama ────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryLime, MaterialTheme.shapes.small)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "FundFlow",
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Header teks ───────────────────────────────────
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text       = "Selamat Datang Kembali",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Masuk ke akun Anda untuk melanjutkan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Form ──────────────────────────────────────────
            FundFlowTextField(
                value         = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label         = "Email atau Username",
                leadingIcon   = Icons.Default.AlternateEmail,
                isError       = uiState.emailError != null,
                errorMessage  = uiState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            FundFlowPasswordField(
                value         = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label         = "Kata Sandi",
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.passwordError != null,
                errorMessage  = uiState.passwordError
            )

            // Lupa kata sandi
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FundFlowTextButton(
                    text    = "Lupa kata sandi?",
                    onClick = { /* TODO: forgot password flow */ },
                    color   = PrimaryLimeDark
                )
            }

            Spacer(Modifier.height(8.dp))

            // Error umum
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = ExpenseRed.copy(alpha = 0.10f)
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text     = uiState.errorMessage!!,
                        color    = ExpenseRed,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            // Tombol Masuk
            FundFlowPrimaryButton(
                text      = "Masuk",
                onClick   = viewModel::login,
                isLoading = uiState.isLoading
            )

            Spacer(Modifier.height(24.dp))

            // Link daftar
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Belum punya akun? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight
                )
                FundFlowTextButton(
                    text    = "Daftar Sekarang",
                    onClick = onNavigateToRegister,
                    color   = PrimaryLimeDark
                )
            }
        }
    }
}