// ============================================================
// feature/pengeluaran/presentation/PengeluaranScreen.kt
// ============================================================
package com.example.fundflow.feature.pengeluaran.presentation

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
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun PengeluaranScreen(
    onNavigateBack: () -> Unit,
    viewModel: PengeluaranViewModel = hiltViewModel()
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
                FundFlowTopBar(title = "Pengeluaran", onNavigateBack = onNavigateBack)
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick        = viewModel::onShowAddSheet,
                    containerColor = ExpenseRed,
                    contentColor   = CardWhite
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Pengeluaran")
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
            // ── Kartu total pengeluaran ────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = ExpenseRed),
                    shape     = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingDown, null, tint = CardWhite.copy(alpha = 0.8f), modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Total Pengeluaran", style = MaterialTheme.typography.bodySmall, color = CardWhite.copy(alpha = 0.85f))
                            Text(
                                CurrencyFormatter.format(uiState.totalPengeluaran),
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
                    label         = "Cari pengeluaran...",
                    leadingIcon   = Icons.Default.Search,
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            if (!uiState.isLoading && uiState.filteredList.isEmpty()) {
                item {
                    EmptyStateView(
                        icon    = Icons.Default.TrendingDown,
                        title   = "Belum ada pengeluaran",
                        message = "Klik tombol + untuk mencatat pengeluaran pertama.",
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp)
                    )
                }
            }

            items(uiState.filteredList, key = { it.pengeluaranId }) { item ->
                PengeluaranListItem(
                    pengeluaran     = item,
                    isSelectionMode = uiState.isSelectionMode,
                    isSelected      = item.pengeluaranId in uiState.selectedIds,
                    onClick         = { viewModel.onItemClick(item.pengeluaranId) },
                    onLongClick     = { viewModel.onItemLongClick(item.pengeluaranId) },
                    onEdit          = { viewModel.onShowEditSheet(item) },
                    onDelete        = { viewModel.onRequestDelete(item.pengeluaranId) }
                )
            }
        }
    }

    if (uiState.showFormSheet) {
        PengeluaranDetailSheet(uiState = uiState, viewModel = viewModel)
    }

    if (uiState.showDeleteDialog) {
        if (uiState.deleteTargetId != null) {
            ConfirmDeleteDialog(
                title = "Hapus Pengeluaran?",
                message = "Data pengeluaran ini akan dihapus secara permanen.",
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        } else {
            ConfirmBatchDeleteDialog(
                itemCount = uiState.selectedIds.size,
                itemName  = "pengeluaran",
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        }
    }
}

@Composable
private fun PengeluaranListItem(
    pengeluaran: Pengeluaran,
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
                .background(ExpenseRed.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.TrendingDown, null, tint = ExpenseRed, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(pengeluaran.deskripsi, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextDark, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pengeluaran.kategori, style = MaterialTheme.typography.bodySmall, color = AccentPurple)
                if (pengeluaran.namaProgram.isNotBlank()) {
                    Text(" · ${pengeluaran.namaProgram}", style = MaterialTheme.typography.bodySmall, color = TextLight, maxLines = 1)
                }
            }
            Text(pengeluaran.tanggal, style = MaterialTheme.typography.bodySmall, color = TextLight)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "- ${CurrencyFormatter.format(pengeluaran.totalNominal)}",
                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ExpenseRed
            )
            if (pengeluaran.quantity > 1) {
                Text(
                    "${pengeluaran.quantity}x ${CurrencyFormatter.format(pengeluaran.hargaSatuan)}",
                    style = MaterialTheme.typography.bodySmall, color = TextLight
                )
            }
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