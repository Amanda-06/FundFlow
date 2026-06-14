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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                if (uiState.isSelectionMode) {
                    FundFlowSelectionTopBar(
                        selectedCount = uiState.selectedIds.size,
                        totalCount    = uiState.filteredList.size,
                        onSelectAll   = viewModel::onSelectAll,
                        onDelete      = viewModel::onRequestBatchDelete,
                        onCancel      = viewModel::onCancelSelection
                    )
                } else {
                    Text(
                        text       = stringResource(R.string.pengeluaran_title),
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onBackground,
                        modifier   = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick        = viewModel::onShowAddSheet,
                    containerColor = ExpenseRed, // tetap — warna brand aksen pengeluaran
                    contentColor   = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.pengeluaran_tambah))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
            contentPadding      = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Kartu total pengeluaran — tetap ExpenseRed (warna brand) ──
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
                        Icon(
                            Icons.Default.TrendingDown, null,
                            tint     = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                stringResource(R.string.pengeluaran_total),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            )
                            Text(
                                CurrencyFormatter.format(uiState.totalPengeluaran),
                                style      = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color      = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }

            item {
                FundFlowTextField(
                    value         = uiState.searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    label         = stringResource(R.string.pengeluaran_search_hint),
                    leadingIcon   = Icons.Default.Search,
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            if (!uiState.isLoading && uiState.filteredList.isEmpty()) {
                item {
                    EmptyStateView(
                        icon     = Icons.Default.TrendingDown,
                        title    = stringResource(R.string.pengeluaran_empty_title),
                        message  = stringResource(R.string.pengeluaran_empty_message),
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
                title     = stringResource(R.string.pengeluaran_delete_title),
                message   = stringResource(R.string.pengeluaran_delete_message),
                onConfirm = viewModel::onConfirmDelete,
                onDismiss = viewModel::onDismissDeleteDialog
            )
        } else {
            ConfirmBatchDeleteDialog(
                itemCount = uiState.selectedIds.size,
                itemName  = stringResource(R.string.pengeluaran_item_name),
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
            Text(
                pengeluaran.deskripsi,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface,
                maxLines   = 1
            )
            Text(
                pengeluaran.kategori,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "- ${CurrencyFormatter.format(pengeluaran.totalNominal)}",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = ExpenseRed
            )
            if (pengeluaran.quantity > 1) {
                Text(
                    "${pengeluaran.quantity}x ${CurrencyFormatter.format(pengeluaran.hargaSatuan)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isSelectionMode) {
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.Edit, null,
                            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = ExpenseRed, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}