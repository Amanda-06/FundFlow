package com.example.fundflow.feature.anggota.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun AnggotaScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnggotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.isSelectionMode) {
                FundFlowSelectionTopBar(
                    selectedCount = uiState.selectedIds.size,
                    totalCount    = uiState.filteredList.size,
                    onSelectAll   = viewModel::onSelectAll,
                    onDelete      = viewModel::onRequestBatchDelete,
                    onCancel      = viewModel::onCancelSelection
                )
            } else {
                FundFlowTopBar(
                    title          = stringResource(R.string.anggota_title),
                    onNavigateBack = onNavigateBack
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick        = viewModel::onShowAddDialog,
                    containerColor = PrimaryLime,
                    contentColor   = TextDark          // tetap: warna brand
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = stringResource(R.string.anggota_add_title))
                }
            }
        },
        // FIX: pakai MaterialTheme.colorScheme.background agar reaktif terhadap tema
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Search Bar ────────────────────────────────────
            FundFlowTextField(
                value         = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                label         = stringResource(R.string.anggota_cari_nama_anggota),
                leadingIcon   = Icons.Default.Search,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── Jumlah anggota ─────────────────────────────────
            Text(
                // FIX: Menghilangkan hardcode teks "anggota" dengan format stringResource dinamis
                text     = stringResource(R.string.anggota_count_format, uiState.filteredList.size),
                style    = MaterialTheme.typography.bodySmall,
                // FIX: reaktif terhadap tema
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ── List ──────────────────────────────────────────
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLime)
                }
            } else if (uiState.filteredList.isEmpty()) {
                EmptyStateView(
                    icon     = Icons.Default.Group,
                    title    = stringResource(R.string.anggota_empty_title),
                    message  = stringResource(R.string.anggota_empty_message),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start  = 16.dp,
                        end    = 16.dp,
                        top    = 4.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredList,
                        key   = { it.anggotaId }
                    ) { anggota ->
                        AnggotaListItem(
                            anggota         = anggota,
                            isSelectionMode = uiState.isSelectionMode,
                            isSelected      = anggota.anggotaId in uiState.selectedIds,
                            onClick         = { viewModel.onItemClick(anggota.anggotaId) },
                            onLongClick     = { viewModel.onItemLongClick(anggota.anggotaId) },
                            onEdit          = { viewModel.onShowEditDialog(anggota) },
                            onDelete        = { viewModel.onRequestDelete(anggota.anggotaId) }
                        )
                    }
                }
            }
        }
    }

    // ── Dialog Tambah / Edit ──────────────────────────────────
    if (uiState.showAddDialog) {
        AnggotaFormDialog(
            isEdit         = uiState.editTarget != null,
            inputNama      = uiState.inputNama,
            inputNamaError = uiState.inputNamaError,
            onNamaChange   = viewModel::onInputNamaChange,
            onSave         = viewModel::onSaveAnggota,
            onDismiss      = viewModel::onDismissAddDialog
        )
    }

    // ── Dialog Konfirmasi Hapus ───────────────────────────────
    if (uiState.showDeleteDialog) {
        if (uiState.deleteTargetId != null) {
            ConfirmDeleteDialog(
                title     = stringResource(R.string.anggota_delete_title),
                message   = stringResource(R.string.anggota_delete_message),
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        } else {
            ConfirmBatchDeleteDialog(
                itemCount    = uiState.selectedIds.size,
                itemName     = stringResource(R.string.anggota_anggota),
                extraWarning = stringResource(R.string.anggota_delete_batch_warning),
                onConfirm    = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        }
    }
}

// ── Item card anggota ─────────────────────────────────────────
@Composable
private fun AnggotaListItem(
    anggota: Anggota,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SelectableListItem(
        isSelectionMode = isSelectionMode,
        isSelected      = isSelected,
        onClick         = onClick,
        onLongClick     = onLongClick
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.inverseSurface,
                    MaterialTheme.shapes.extraLarge
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = anggota.namaAnggota.take(1).uppercase(),
                style      = MaterialTheme.typography.titleSmall,
                color      = MaterialTheme.colorScheme.inverseOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text       = anggota.namaAnggota,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier.weight(1f)
        )
        if (!isSelectionMode) {
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.common_edit),
                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.common_delete),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── Dialog Form Tambah / Edit ─────────────────────────────────
@Composable
private fun AnggotaFormDialog(
    isEdit: Boolean,
    inputNama: String,
    inputNamaError: String?,
    onNamaChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text  = if (isEdit) stringResource(R.string.anggota_edit_title) else stringResource(R.string.anggota_add_title),
                style = MaterialTheme.typography.titleLarge,
                // FIX: reaktif terhadap tema
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            FundFlowTextField(
                value         = inputNama,
                onValueChange = onNamaChange,
                label         = stringResource(R.string.anggota_nama_label),
                leadingIcon   = Icons.Default.Person,
                isError       = inputNamaError != null,
                errorMessage  = inputNamaError
            )
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLime,
                    contentColor   = TextDark    // tetap: warna brand
                )
            ) {
                Text(stringResource(R.string.anggota_simpan), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {

                Text(stringResource(R.string.anggota_batal), color = MaterialTheme.colorScheme.onSurface)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}