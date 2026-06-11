package com.example.fundflow.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun EditProfilScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarState.showSnackbar("Profil berhasil diperbarui")
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarState.showSnackbar(it); viewModel.clearError() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = { FundFlowTopBar(title = "Edit Profil", onNavigateBack = onNavigateBack) },
        containerColor = AppBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
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
            FundFlowTextField(
                value         = uiState.namaLengkap,
                onValueChange = viewModel::onNamaLengkapChange,
                label         = "Nama Lengkap",
                leadingIcon   = Icons.Default.Person,
                isError       = uiState.namaLengkapError != null,
                errorMessage  = uiState.namaLengkapError
            )

            FundFlowTextField(
                value         = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label         = "Username",
                leadingIcon   = Icons.Default.AlternateEmail,
                isError       = uiState.usernameError != null,
                errorMessage  = uiState.usernameError
            )

            // Email — read-only (perubahan email perlu verifikasi terpisah)
            FundFlowTextField(
                value         = uiState.email,
                onValueChange = {},
                label         = "Email",
                leadingIcon   = Icons.Default.AlternateEmail,
                enabled       = false
            )

            FundFlowTextField(
                value         = uiState.namaOrganisasi,
                onValueChange = viewModel::onNamaOrganisasiChange,
                label         = "Nama Organisasi",
                leadingIcon   = Icons.Default.Business
            )

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                text      = "Simpan Perubahan",
                onClick   = viewModel::onSave,
                isLoading = uiState.isSaving
            )
        }
    }
}