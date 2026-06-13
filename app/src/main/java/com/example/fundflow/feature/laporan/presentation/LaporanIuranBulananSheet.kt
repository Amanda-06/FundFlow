// feature/laporan/presentation/LaporanIuranBulananSheet.kt
package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.ui.components.FundFlowBottomSheet
import com.example.fundflow.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanIuranBulananSheet(
    uiState: LaporanState,
    viewModel: LaporanViewModel
) {
    // FIX: Dapatkan Locale sistem perangkat secara dinamis agar reaktif terhadap perubahan bahasa
    val configuration = LocalConfiguration.current
    val currentLocale = remember(configuration) {
        ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
    }

    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Text(
            stringResource(R.string.laporan_iuran_bulanan),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface
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
            colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    stringResource(R.string.laporan_rincian_iuran),
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))

                // Header tabel
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(
                        stringResource(R.string.common_no),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        stringResource(R.string.laporan_bulan),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        stringResource(R.string.laporan_jumlah),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                laporan.rincianBulan.forEachIndexed { index, rincian ->
                    // FIX: Format data angka bulan & tahun mentah menjadi string nama bulan mengikuti bahasa sistem
                    val formattedMonthLabel = remember(rincian, currentLocale) {
                        LocalDate.of(rincian.tahun, rincian.bulan, 1)
                            .format(DateTimeFormatter.ofPattern("MMMM yyyy", currentLocale))
                            .replaceFirstChar { it.uppercase() }
                    }

                    Row(
                        modifier          = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}",
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(32.dp)
                        )
                        Text(
                            formattedMonthLabel, // Menggunakan label teks dinamis yang sudah diformat aman
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            CurrencyFormatter.format(rincian.jumlah),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                }

                // Total — IncomeGreen tetap: warna semantik
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IncomeGreen.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.laporan_total),
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        CurrencyFormatter.format(laporan.totalKeseluruhan),
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = IncomeGreenDark // tetap: warna semantik
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

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
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = PdfRed), // PdfRed tetap: warna semantik PDF
            shape    = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.laporan_export_pdf), fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick  = viewModel::onExportExcel,
            enabled  = !uiState.isExporting,
            modifier = Modifier.weight(1f).height(48.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = IncomeGreen, contentColor = Color.White), // IncomeGreen & White tetap: warna semantik Excel
            shape    = MaterialTheme.shapes.medium
        ) {
            if (uiState.isExporting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.laporan_export_excel), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}