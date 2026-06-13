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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccessState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // FIX: reaktif terhadap tema (menggantikan BackgroundKrem yang hardcoded)
            .background(MaterialTheme.colorScheme.background)
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
                        // PrimaryLime tetap: warna brand
                        .background(PrimaryLime, MaterialTheme.shapes.small)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    // FIX: Menggunakan stringResource app_name yang sudah terdefinisi di proyek
                    text       = stringResource(R.string.app_name),
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Header teks ───────────────────────────────────
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    // FIX: Lokalisasi judul selamat datang kembali
                    text       = stringResource(R.string.login_welcome_title),
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    // FIX: Lokalisasi teks instruksi login
                    text  = stringResource(R.string.login_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Form ──────────────────────────────────────────
            FundFlowTextField(
                value           = uiState.email,
                onValueChange   = viewModel::onEmailChange,
                // FIX: Lokalisasi label Email atau Username
                label           = stringResource(R.string.login_label_email_username),
                leadingIcon     = Icons.Default.AlternateEmail,
                isError         = uiState.emailError != null,
                errorMessage    = uiState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            FundFlowPasswordField(
                value         = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                // FIX: Lokalisasi label Kata Sandi
                label         = stringResource(R.string.login_label_password),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.passwordError != null,
                errorMessage  = uiState.passwordError
            )

            // Lupa kata sandi
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                FundFlowTextButton(
                    // FIX: Lokalisasi teks tombol lupa kata sandi
                    text    = stringResource(R.string.login_btn_forgot_password),
                    onClick = { /* TODO: forgot password flow */ },
                    color   = PrimaryLimeDark   // tetap: warna brand
                )
            }

            Spacer(Modifier.height(8.dp))

            // Error umum
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = ExpenseRed.copy(alpha = 0.10f)   // tetap: semantik
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text     = uiState.errorMessage!!,
                        color    = ExpenseRed,   // tetap: warna semantik error
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            FundFlowPrimaryButton(
                // FIX: Lokalisasi teks tombol Masuk
                text      = stringResource(R.string.login_btn_submit),
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
                    // FIX: Lokalisasi teks kaki footer ajakan daftar
                    text  = stringResource(R.string.login_footer_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FundFlowTextButton(
                    // FIX: Lokalisasi teks tombol tautan daftar sekarang
                    text    = stringResource(R.string.login_footer_register_now),
                    onClick = onNavigateToRegister,
                    color   = PrimaryLimeDark   // tetap: warna brand
                )
            }
        }
    }
}