// feature/profile/presentation/EditProfilScreen.kt
// ============================================================
package com.example.fundflow.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun EditProfilScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfilViewModel = hiltViewModel()
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    // FIX: Lokalisasi teks pesan sukses di snackbar
    val successMessage = stringResource(R.string.edit_profile_success)
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarState.showSnackbar(successMessage)
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarState) },
        // FIX: Lokalisasi judul TopBar
        topBar         = { FundFlowTopBar(title = stringResource(R.string.edit_profile_title), onNavigateBack = onNavigateBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryLime)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Informasi Profil ─────────────────────────────
            FundFlowTextField(
                value         = uiState.namaLengkap,
                onValueChange = viewModel::onNamaLengkapChange,
                // FIX: Lokalisasi label Nama Lengkap
                label         = stringResource(R.string.edit_profile_label_nama_lengkap),
                leadingIcon   = Icons.Default.Person,
                isError       = uiState.namaLengkapError != null,
                errorMessage  = uiState.namaLengkapError
            )

            FundFlowTextField(
                value         = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                // FIX: Lokalisasi label Username
                label         = stringResource(R.string.edit_profile_label_username),
                leadingIcon   = Icons.Default.AlternateEmail,
                isError       = uiState.usernameError != null,
                errorMessage  = uiState.usernameError
            )

            // Email — read-only
            FundFlowTextField(
                value         = uiState.email,
                onValueChange = {},
                // FIX: Lokalisasi label Email
                label         = stringResource(R.string.edit_profile_label_email),
                leadingIcon   = Icons.Default.AlternateEmail,
                enabled       = false
            )

            FundFlowTextField(
                value         = uiState.namaOrganisasi,
                onValueChange = viewModel::onNamaOrganisasiChange,
                // FIX: Lokalisasi label Nama Organisasi
                label         = stringResource(R.string.edit_profile_label_nama_organisasi),
                leadingIcon   = Icons.Default.Business
            )

            // ── Divider + Label Section Ubah Password ────────
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(4.dp))

            Text(
                // FIX: Lokalisasi judul section ubah password
                text  = stringResource(R.string.edit_profile_section_ubah_password),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                // FIX: Lokalisasi teks keterangan peringatan password
                text  = stringResource(R.string.edit_profile_password_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ── Password Saat Ini ─────────────────────────────
            FundFlowTextField(
                value         = uiState.passwordSaatIni,
                onValueChange = viewModel::onPasswordSaatIniChange,
                // FIX: Lokalisasi label password saat ini
                label         = stringResource(R.string.edit_profile_label_password_saat_ini),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.passwordSaatIniError != null,
                errorMessage  = uiState.passwordSaatIniError,
                visualTransformation = if (uiState.passwordSaatIniVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordSaatIniVisible) {
                        Icon(
                            imageVector = if (uiState.passwordSaatIniVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            // FIX: Lokalisasi contentDescription keterbacaan password
                            contentDescription = if (uiState.passwordSaatIniVisible)
                                stringResource(R.string.common_hide_password)
                            else
                                stringResource(R.string.common_show_password)
                        )
                    }
                }
            )

            // ── Password Baru ─────────────────────────────────
            FundFlowTextField(
                value         = uiState.passwordBaru,
                onValueChange = viewModel::onPasswordBaruChange,
                // FIX: Lokalisasi label password baru
                label         = stringResource(R.string.edit_profile_label_password_baru),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.passwordBaruError != null,
                errorMessage  = uiState.passwordBaruError,
                visualTransformation = if (uiState.passwordBaruVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::onTogglePasswordBaruVisible) {
                        Icon(
                            imageVector = if (uiState.passwordBaruVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            // FIX: Lokalisasi contentDescription keterbacaan password baru
                            contentDescription = if (uiState.passwordBaruVisible)
                                stringResource(R.string.common_hide_password)
                            else
                                stringResource(R.string.common_show_password)
                        )
                    }
                }
            )

            // ── Konfirmasi Password ───────────────────────────
            FundFlowTextField(
                value         = uiState.konfirmasiPassword,
                onValueChange = viewModel::onKonfirmasiPasswordChange,
                // FIX: Lokalisasi label konfirmasi password
                label         = stringResource(R.string.edit_profile_label_konfirmasi_password),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.konfirmasiPasswordError != null,
                errorMessage  = uiState.konfirmasiPasswordError,
                visualTransformation = if (uiState.konfirmasiPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::onToggleKonfirmasiPasswordVisible) {
                        Icon(
                            imageVector = if (uiState.konfirmasiPasswordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            // FIX: Lokalisasi contentDescription keterbacaan konfirmasi password
                            contentDescription = if (uiState.konfirmasiPasswordVisible)
                                stringResource(R.string.common_hide_password)
                            else
                                stringResource(R.string.common_show_password)
                        )
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                // FIX: Lokalisasi teks tombol simpan
                text      = stringResource(R.string.edit_profile_btn_simpan),
                onClick   = viewModel::onSave,
                isLoading = uiState.isSaving
            )
        }
    }
}