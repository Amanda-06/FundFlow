// feature/laporan/presentation/LaporanDetailKeuanganSheet.kt
// ============================================================
package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.laporan.domain.model.ItemDetailKeuangan
import com.example.fundflow.ui.components.FundFlowBottomSheet
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanDetailKeuanganSheet(
    uiState: LaporanState,
    viewModel: LaporanViewModel
) {
    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Text(
            stringResource(R.string.laporan_detail_keuangan_laporan_detail_keuangan),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            // FIX: reaktif terhadap tema
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))

        if (uiState.isGenerating || uiState.laporanDetail == null) {
            Box(Modifier
                .fillMaxWidth()
                .padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLime)
            }
            return@FundFlowBottomSheet
        }

        val laporan = uiState.laporanDetail

        Text(
            laporan.periode,
            style = MaterialTheme.typography.bodySmall,
            // FIX: reaktif terhadap tema
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        // ── Ringkasan: Pemasukan / Pengeluaran ────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // color (IncomeGreen / ExpenseRed) tetap: warna semantik
            SummaryBox(
                modifier = Modifier.weight(1f),
                label    = stringResource(R.string.laporan_detail_keuangan_pemasukan),
                value    = CurrencyFormatter.formatShort(laporan.totalPemasukan),
                color    = IncomeGreen,
                icon     = Icons.Default.TrendingUp
            )
            SummaryBox(
                modifier = Modifier.weight(1f),
                label    = stringResource(R.string.laporan_detail_keuangan_pengeluaran),
                value    = CurrencyFormatter.formatShort(laporan.totalPengeluaran),
                color    = ExpenseRed,
                icon     = Icons.Default.TrendingDown
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Saldo Akhir ────────────────────────────────────────
        // HeaderGreen tetap: warna brand
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = HeaderGreen.copy(alpha = 0.25f)),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.laporan_detail_keuangan_saldo_akhir),
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    CurrencyFormatter.format(laporan.saldoAkhir),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Daftar Transaksi ───────────────────────────────────
        Box(modifier = Modifier.heightIn(max = 320.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (laporan.daftarPemasukan.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.laporan_detail_keuangan_pemasukan1),
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = IncomeGreen,   // tetap: warna semantik
                            modifier   = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(laporan.daftarPemasukan) { item -> TransaksiRow(item) }
                }

                if (laporan.daftarPengeluaran.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.laporan_detail_keuangan_pengeluaran1),
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = ExpenseRed,   // tetap: warna semantik
                            modifier   = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(laporan.daftarPengeluaran) { item -> TransaksiRow(item) }
                }

                if (laporan.daftarPemasukan.isEmpty() && laporan.daftarPengeluaran.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.laporan_detail_keuangan_belum_ada_transaksi_pada_periode_ini),
                                // FIX: reaktif terhadap tema
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ExportButtonsRow(uiState = uiState, viewModel = viewModel)
    }
}

// ── Komponen ringkasan (Pemasukan/Pengeluaran) ────────────────
@Composable
private fun SummaryBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,   // warna semantik dari caller
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    // Background color.copy(alpha) tetap: warna semantik per kategori
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f)),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = color,   // tetap: warna semantik
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = color   // tetap: warna semantik
            )
        }
    }
}

// ── Baris transaksi ────────────────────────────────────────────
@Composable
private fun TransaksiRow(item: ItemDetailKeuangan) {
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
                        // tetap: warna semantik income/expense
                        if (item.isIncome) IncomeGreen.copy(alpha = 0.12f) else ExpenseRed.copy(
                            alpha = 0.12f
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (item.isIncome) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    // tetap: warna semantik income/expense
                    tint     = if (item.isIncome) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.deskripsi,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1
                )
                Row {
                    // AccentPurple tetap: warna brand/aksen untuk keterangan
                    Text(item.keterangan, style = MaterialTheme.typography.bodySmall, color = AccentPurple, maxLines = 1)
                    Text(
                        // FIX: Menggunakan stringResource format agar tanda baca titik tidak hardcode
                        text  = stringResource(R.string.laporan_detail_date_format, item.tanggal),
                        style = MaterialTheme.typography.bodySmall,
                        // FIX: reaktif terhadap tema
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                "${if (item.isIncome) "+" else "-"} ${CurrencyFormatter.format(item.nominal)}",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                // tetap: warna semantik income/expense
                color      = if (item.isIncome) IncomeGreen else ExpenseRed
            )
        }
    }
}