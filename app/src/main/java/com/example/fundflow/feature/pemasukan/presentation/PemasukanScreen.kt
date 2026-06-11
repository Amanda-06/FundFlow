package com.example.fundflow.feature.pemasukan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun PemasukanScreen(
    onNavigateBack: () -> Unit,
    viewModel: PemasukanViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState  = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let   { snackbarState.showSnackbar(it); viewModel.clearMessages() }
        uiState.successMessage?.let { snackbarState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
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
                FundFlowTopBar(title = "Pemasukan", onNavigateBack = onNavigateBack)
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick        = viewModel::onShowAddSheet,
                    containerColor = IncomeGreen,
                    contentColor   = CardWhite
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Pemasukan")
                }
            }
        },
        containerColor = AppBackground
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Kartu total pemasukan ──────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = IncomeGreen),
                    shape     = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint               = CardWhite.copy(alpha = 0.8f),
                            modifier           = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Total Pemasukan",
                                style = MaterialTheme.typography.bodySmall,
                                color = CardWhite.copy(alpha = 0.85f)
                            )
                            Text(
                                CurrencyFormatter.format(uiState.totalPemasukan),
                                style      = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color      = CardWhite
                            )
                        }
                    }
                }
            }

            // ── Search bar ────────────────────────────────────
            item {
                FundFlowTextField(
                    value         = uiState.searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    label         = "Cari pemasukan...",
                    leadingIcon   = Icons.Default.Search,
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            // ── Empty state ───────────────────────────────────
            if (!uiState.isLoading && uiState.filteredList.isEmpty()) {
                item {
                    EmptyStateView(
                        icon    = Icons.Default.TrendingUp,
                        title   = "Belum ada pemasukan",
                        message = "Klik tombol + untuk mencatat pemasukan pertama.",
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
                    )
                }
            }

            // ── List ──────────────────────────────────────────
            items(uiState.filteredList, key = { it.pemasukanId }) { item ->
                PemasukanListItem(
                    pemasukan       = item,
                    isSelectionMode = uiState.isSelectionMode,
                    isSelected      = item.pemasukanId in uiState.selectedIds,
                    onClick         = { viewModel.onItemClick(item.pemasukanId) },
                    onLongClick     = { viewModel.onItemLongClick(item.pemasukanId) },
                    onEdit          = { viewModel.onShowEditSheet(item) },
                    onDelete        = { viewModel.onRequestDelete(item.pemasukanId) }
                )
            }
        }
    }

    // ── Bottom Sheet Form ─────────────────────────────────────
    if (uiState.showFormSheet) {
        PemasukanDetailSheet(
            uiState   = uiState,
            viewModel = viewModel
        )
    }

    // ── Konfirmasi Hapus ──────────────────────────────────────
    if (uiState.showDeleteDialog) {
        if (uiState.deleteTargetId != null) {
            ConfirmDeleteDialog(
                title     = "Hapus Pemasukan?",
                message   = "Data pemasukan ini akan dihapus secara permanen.",
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        } else {
            ConfirmBatchDeleteDialog(
                itemCount = uiState.selectedIds.size,
                itemName  = "pemasukan",
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        }
    }
}

// ── List Item Pemasukan ───────────────────────────────────────
@Composable
private fun PemasukanListItem(
    pemasukan: Pemasukan,
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
        // Ikon sumber
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(IncomeGreen.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.TrendingUp, null, tint = IncomeGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                pemasukan.deskripsi,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = TextDark,
                maxLines   = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pemasukan.sumber,
                    style   = MaterialTheme.typography.bodySmall,
                    color   = AccentPurple,
                    maxLines = 1
                )
                Text(
                    " · ${pemasukan.tanggal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLight
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                "+ ${CurrencyFormatter.format(pemasukan.nominal)}",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = IncomeGreen
            )
            if (!isSelectionMode) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, null, tint = TextLight, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ExpenseRed, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}