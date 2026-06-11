// ============================================================
// feature/anggota/presentation/AnggotaScreen.kt
// ============================================================
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun AnggotaScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnggotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Error snackbar
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
                    title          = "Manajemen Anggota",
                    onNavigateBack = onNavigateBack
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick           = viewModel::onShowAddDialog,
                    containerColor    = PrimaryLime,
                    contentColor      = TextDark
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Anggota")
                }
            }
        },
        containerColor = AppBackground
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
                label         = "Cari nama anggota...",
                leadingIcon   = Icons.Default.Search,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── Jumlah anggota ─────────────────────────────────
            Text(
                text     = "${uiState.filteredList.size} anggota",
                style    = MaterialTheme.typography.bodySmall,
                color    = TextLight,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ── List ──────────────────────────────────────────
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLime)
                }
            } else if (uiState.filteredList.isEmpty()) {
                EmptyStateView(
                    icon    = Icons.Default.Group,
                    title   = "Belum ada anggota",
                    message = "Klik tombol + untuk menambahkan anggota pertama.",
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
            isEdit        = uiState.editTarget != null,
            inputNama     = uiState.inputNama,
            inputNamaError= uiState.inputNamaError,
            onNamaChange  = viewModel::onInputNamaChange,
            onSave        = viewModel::onSaveAnggota,
            onDismiss     = viewModel::onDismissAddDialog
        )
    }

    // ── Dialog Konfirmasi Hapus ───────────────────────────────
    if (uiState.showDeleteDialog) {
        if (uiState.deleteTargetId != null) {
            ConfirmDeleteDialog(
                title     = "Hapus Anggota?",
                message   = "Data iuran anggota ini untuk seluruh bulan juga akan ikut terhapus secara permanen.",
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        } else {
            ConfirmBatchDeleteDialog(
                itemCount    = uiState.selectedIds.size,
                itemName     = "anggota",
                extraWarning = "Semua data iuran dari anggota yang dipilih juga akan ikut terhapus.",
                onConfirm    = viewModel::onConfirmDelete,
                onDismiss    = viewModel::onDismissDeleteDialog
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
        // Avatar inisial
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(NavBackground, MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = anggota.namaAnggota.take(1).uppercase(),
                style      = MaterialTheme.typography.titleSmall,
                color      = CardWhite,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text       = anggota.namaAnggota,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color      = TextDark,
            modifier   = Modifier.weight(1f)
        )
        // Aksi edit / delete — hanya tampil saat bukan selection mode
        if (!isSelectionMode) {
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextLight, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = ExpenseRed, modifier = Modifier.size(16.dp))
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
                text  = if (isEdit) "Edit Anggota" else "Tambah Anggota",
                style = MaterialTheme.typography.titleLarge,
                color = TextDark
            )
        },
        text = {
            FundFlowTextField(
                value         = inputNama,
                onValueChange = onNamaChange,
                label         = "Nama Anggota",
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
                    contentColor   = TextDark
                )
            ) {
                Text("Simpan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal", color = TextDark)
            }
        },
        containerColor = CardWhite
    )
}
