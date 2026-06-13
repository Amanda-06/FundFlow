package com.example.fundflow.feature.iuran.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.usecase.MonthOption
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IuranScreen(
    onNavigateToAnggota: () -> Unit,
    viewModel: IuranViewModel = hiltViewModel()
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
            TopAppBar(
                title = {
                    Text(
                        "Iuran Anggota",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onBackground // FIX: was TextDark
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // FIX: was AppBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onNavigateToAnggota,
                containerColor = MaterialTheme.colorScheme.inverseSurface, // FIX: was NavBackground
                contentColor   = MaterialTheme.colorScheme.inverseOnSurface // FIX: was CardWhite
            ) {
                Icon(Icons.Default.Group, contentDescription = "Manajemen Anggota")
            }
        },
        containerColor = MaterialTheme.colorScheme.background // FIX: was AppBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            MonthSelector(
                label    = uiState.selectedMonth?.label ?: "Pilih Periode",
                onClick  = viewModel::onShowMonthPicker,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            SummaryRow(
                lunas      = uiState.summary.lunasCount,
                belumBayar = uiState.summary.belumBayarCount,
                terkumpul  = uiState.summary.totalTerkumpul,
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            FundFlowTextField(
                value         = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                label         = "Cari nama anggota...",
                leadingIcon   = Icons.Default.Search,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipItem("Semua",      uiState.activeFilter == IuranFilter.SEMUA)      { viewModel.onFilterChange(IuranFilter.SEMUA) }
                FilterChipItem("Lunas",      uiState.activeFilter == IuranFilter.LUNAS)      { viewModel.onFilterChange(IuranFilter.LUNAS) }
                FilterChipItem("Belum Bayar",uiState.activeFilter == IuranFilter.BELUM_BAYAR){ viewModel.onFilterChange(IuranFilter.BELUM_BAYAR) }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryLime)
                }
            } else if (uiState.filteredList.isEmpty()) {
                EmptyStateView(
                    icon     = Icons.Default.Receipt,
                    title    = "Tidak ada data",
                    message  = "Tidak ada anggota yang sesuai dengan pencarian/filter.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredList, key = { it.anggotaId }) { iuran ->
                        IuranListItem(iuran = iuran, onClick = { viewModel.onItemClick(iuran) })
                    }
                }
            }
        }
    }

    if (uiState.showMonthPicker) {
        MonthPickerDialog(
            options   = uiState.availableMonths,
            selected  = uiState.selectedMonth,
            onSelect  = viewModel::onSelectMonth,
            onDismiss = viewModel::onDismissMonthPicker
        )
    }

    if (uiState.showDetailSheet && uiState.selectedIuran != null) {
        IuranDetailSheet(uiState = uiState, viewModel = viewModel)
    }
}

// ── Month Selector ─────────────────────────────────────────────
@Composable
private fun MonthSelector(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        onClick   = onClick,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // FIX: was CardWhite
        ),
        shape     = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onSurface, // FIX: was TextDark
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    label,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface // FIX: was TextDark
                )
            }
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
            )
        }
    }
}

// ── Summary Cards ───────────────────────────────────────────────
@Composable
private fun SummaryRow(lunas: Int, belumBayar: Int, terkumpul: Double, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // FIX: was CardWhite
        ),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(value = "$lunas",                                        label = "Lunas",      color = IncomeGreen)
            VerticalDivider(modifier = Modifier.height(36.dp), color = MaterialTheme.colorScheme.outline) // FIX: was BorderGray
            SummaryItem(value = "$belumBayar",                                   label = "Belum Bayar",color = ExpenseRed)
            VerticalDivider(modifier = Modifier.height(36.dp), color = MaterialTheme.colorScheme.outline)
            SummaryItem(value = CurrencyFormatter.formatShort(terkumpul),        label = "Terkumpul",  color = IuranBlue)
        }
    }
}

@Composable
private fun SummaryItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
        )
    }
}

// ── Filter Chip ──────────────────────────────────────────────────
@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val unselectedBg = MaterialTheme.colorScheme.surfaceVariant // FIX: was hardcoded Color(0xFFE2E8F0)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryLime else unselectedBg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelMedium,
            color      = if (isSelected) TextDark else MaterialTheme.colorScheme.onSurfaceVariant, // FIX: was TextLight
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ── List Item Anggota ────────────────────────────────────────────
@Composable
private fun IuranListItem(iuran: Iuran, onClick: () -> Unit) {
    FundFlowCard(onClick = onClick, elevation = 1.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (iuran.statusBayar) IncomeGreen.copy(alpha = 0.12f)
                        else ExpenseRed.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (iuran.statusBayar) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint               = if (iuran.statusBayar) IncomeGreen else ExpenseRed,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    iuran.namaAnggota,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface // FIX: was TextDark
                )
                Text(
                    text  = if (iuran.statusBayar) (iuran.tanggalBayar ?: "-") else "Belum bayar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text       = if (iuran.statusBayar) CurrencyFormatter.format(iuran.nominal) else "Rp 0",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (iuran.statusBayar) MaterialTheme.colorScheme.onSurface // FIX: was TextDark
                    else MaterialTheme.colorScheme.onSurfaceVariant             // FIX: was TextMuted
                )
                if (iuran.statusBayar && iuran.metodePembayaran.isNotBlank()) {
                    Text(
                        iuran.metodePembayaran,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
                    )
                }
            }
        }
    }
}

// ── Month Picker Dialog ───────────────────────────────────────────
@Composable
private fun MonthPickerDialog(
    options: List<MonthOption>,
    selected: MonthOption?,
    onSelect: (MonthOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Pilih Periode",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface // FIX: was TextDark
            )
        },
        text = {
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyColumn {
                    items(options, key = { it.key }) { option ->
                        val isSelected = option.key == selected?.key
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            onClick  = { onSelect(option) },
                            shape    = MaterialTheme.shapes.small,
                            color    = if (isSelected) PrimaryLime.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface // FIX: was CardWhite
                        ) {
                            Text(
                                option.label,
                                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                color      = if (isSelected) PrimaryLimeDark
                                else MaterialTheme.colorScheme.onSurface, // FIX: was TextDark
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = MaterialTheme.colorScheme.onSurface) // FIX: was TextDark
            }
        },
        containerColor = MaterialTheme.colorScheme.surface // FIX: was CardWhite
    )
}