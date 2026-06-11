// ============================================================
// feature/laporan/presentation/LaporanIuranBulananSheet.kt
// ============================================================
package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.ui.components.FundFlowBottomSheet
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanIuranBulananSheet(
    uiState: LaporanState,
    viewModel: LaporanViewModel
) {
    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Text(
            "Laporan Iuran Bulanan",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color      = TextDark
        )
        Spacer(Modifier.height(16.dp))

        if (uiState.isGenerating || uiState.laporanIuran == null) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLime)
            }
            return@FundFlowBottomSheet
        }

        val laporan = uiState.laporanIuran

        // ── Tabel Rincian ─────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = CardWhite),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Rincian Iuran Bulanan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(Modifier.height(8.dp))

                // Header tabel
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text("No",    style = MaterialTheme.typography.labelSmall, color = TextLight, modifier = Modifier.width(32.dp))
                    Text("Bulan", style = MaterialTheme.typography.labelSmall, color = TextLight, modifier = Modifier.weight(1f))
                    Text("Jumlah (Rp)", style = MaterialTheme.typography.labelSmall, color = TextLight)
                }
                HorizontalDivider(color = BorderGray)

                laporan.rincianBulan.forEachIndexed { index, rincian ->
                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}", style = MaterialTheme.typography.bodySmall, color = TextDark, modifier = Modifier.width(32.dp))
                        Text(rincian.bulan, style = MaterialTheme.typography.bodySmall, color = TextDark, modifier = Modifier.weight(1f))
                        Text(CurrencyFormatter.format(rincian.jumlah), style = MaterialTheme.typography.bodySmall, color = TextDark)
                    }
                    HorizontalDivider(color = BorderGray.copy(alpha = 0.5f))
                }

                // Total
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .background(IncomeGreen.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        CurrencyFormatter.format(laporan.totalKeseluruhan),
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = IncomeGreenDark
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Tombol Export ─────────────────────────────────────
        ExportButtonsRow(uiState = uiState, viewModel = viewModel)
    }
}

/** Komponen baris tombol Export PDF & Export Excel — dipakai di semua sheet laporan */
@Composable
fun ExportButtonsRow(uiState: LaporanState, viewModel: LaporanViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick  = viewModel::onExportPdf,
            enabled  = !uiState.isExporting,
            modifier = Modifier.weight(1f).height(48.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = PdfRed),
            shape    = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Export PDF", fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick  = viewModel::onExportExcel,
            enabled  = !uiState.isExporting,
            modifier = Modifier.weight(1f).height(48.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = IncomeGreen, contentColor = Color.White),
            shape    = MaterialTheme.shapes.medium
        ) {
            if (uiState.isExporting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Export Excel", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}