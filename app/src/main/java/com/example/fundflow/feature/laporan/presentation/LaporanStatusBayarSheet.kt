// feature/laporan/presentation/LaporanStatusBayarSheet.kt
// ============================================================
package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.laporan.domain.model.StatusBayarAnggota
import com.example.fundflow.ui.components.FundFlowBottomSheet
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanStatusBayarSheet(
    uiState: LaporanState,
    viewModel: LaporanViewModel
) {
    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Text(
            stringResource(R.string.laporan_status_bayar_kas),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            // FIX: reaktif terhadap tema
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(12.dp))

        // ── Month selector ────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth(),
            onClick   = viewModel::onShowStatusMonthPicker,
            // FIX: reaktif terhadap tema
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape     = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        // FIX: reaktif terhadap tema
                        tint     = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        uiState.selectedMonthForStatus?.label ?: stringResource(R.string.pilih_bulan),
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        // FIX: reaktif terhadap tema
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = null,
                    // FIX: reaktif terhadap tema
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (uiState.isGenerating || uiState.laporanStatus == null) {
            Box(Modifier
                .fillMaxWidth()
                .padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLime)
            }
            return@FundFlowBottomSheet
        }

        val laporan = uiState.laporanStatus

        // ── Summary ───────────────────────────────────────────
        // Warna value (IncomeGreen, ExpenseRed, IuranBlue) tetap: warna semantik
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusSummaryItem(value = "${laporan.totalLunas}",    label = stringResource(R.string.laporan_status_bayar_kas_lunas), color = IncomeGreen)
            StatusSummaryItem(value = "${laporan.totalBelumBayar}", label = stringResource(R.string.laporan_status_bayar_kas_belum_bayar), color = ExpenseRed)
            StatusSummaryItem(value = CurrencyFormatter.formatShort(laporan.totalTerkumpul), label = stringResource(
                R.string.laporan_status_bayar_kas_terkumpul
            ), color = IuranBlue)
        }

        Spacer(Modifier.height(12.dp))

        // ── Daftar anggota ────────────────────────────────────
        Box(modifier = Modifier.heightIn(max = 320.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(laporan.rincianAnggota) { item ->
                    StatusBayarRow(item)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ExportButtonsRow(uiState = uiState, viewModel = viewModel)
    }

    // ── Dialog Pilih Bulan ────────────────────────────────────
    if (uiState.showStatusMonthPicker) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissStatusMonthPicker,
            title = {
                Text(
                    stringResource(R.string.pilih_bulan2),
                    style = MaterialTheme.typography.titleLarge,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    LazyColumn {
                        items(uiState.availableMonths) { month ->
                            val isSelected = month.key == uiState.selectedMonthForStatus?.key
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick  = { viewModel.onSelectStatusMonth(month) },
                                shape    = MaterialTheme.shapes.small,
                                // FIX: unselected background reaktif terhadap tema
                                color    = if (isSelected) PrimaryLime.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    month.label,
                                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    // FIX: unselected text reaktif terhadap tema
                                    color      = if (isSelected) PrimaryLimeDark
                                    else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDismissStatusMonthPicker) {
                    // FIX: reaktif terhadap tema
                    Text(stringResource(R.string.laporan_status_bayar_kas_tutup), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            // FIX: reaktif terhadap tema
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun StatusSummaryItem(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color   // warna semantik, tetap dari caller
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            // FIX: reaktif terhadap tema
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusBayarRow(item: StatusBayarAnggota) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        // FIX: reaktif terhadap tema
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape     = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        // tetap: warna semantik status bayar
                        if (item.statusBayar) IncomeGreen.copy(alpha = 0.12f) else ExpenseRed.copy(
                            alpha = 0.12f
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (item.statusBayar) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    // tetap: warna semantik status bayar
                    tint     = if (item.statusBayar) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                item.namaAnggota,
                style    = MaterialTheme.typography.bodyMedium,
                // FIX: reaktif terhadap tema
                color    = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                if (item.statusBayar) CurrencyFormatter.format(item.nominal) else "-",
                style      = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                // Lunas → TextDark sudah tidak dipakai; FIX reaktif. Belum bayar → onSurfaceVariant
                color      = if (item.statusBayar) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}