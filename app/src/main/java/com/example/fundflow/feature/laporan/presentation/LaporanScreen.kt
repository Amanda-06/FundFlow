// ============================================================
// feature/laporan/presentation/LaporanScreen.kt
// ============================================================
package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Ini sudah mencakup Icons.Default / Icons.Filled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    viewModel: LaporanViewModel = hiltViewModel()
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
                title = { Text("Laporan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = TextDark) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Export Laporan Header Card ─────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = HeaderGreen.copy(alpha = 0.25f)),
                shape     = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(HeaderGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = TextDark)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Export Laporan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextDark)
                        Text(
                            "Pilih jenis laporan untuk melihat preview dan export ke PDF atau Excel",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLight
                        )
                    }
                }
            }

            // ── Section Label ──────────────────────────────────
            Text(
                "JENIS LAPORAN",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color      = TextLight,
                modifier   = Modifier.padding(top = 8.dp, start = 4.dp)
            )

            // ── List Jenis Laporan ──────────────────────────────
            LaporanMenuItem(
                icon        = Icons.Default.Groups,
                iconBgColor = IncomeGreen.copy(alpha = 0.12f),
                iconColor   = IncomeGreen,
                title       = "Laporan Iuran Bulanan",
                subtitle    = "Rekap iuran anggota per bulan dan total keseluruhan iuran",
                onClick     = viewModel::onShowLaporanIuran
            )
            LaporanMenuItem(
                icon        = Icons.Default.BarChart,
                iconBgColor = ReportOrange.copy(alpha = 0.12f),
                iconColor   = ReportOrange,
                title       = "Laporan Status Bayar Kas",
                subtitle    = "Status pembayaran kas seluruh anggota dari bulan ke bulan",
                onClick     = viewModel::onShowLaporanStatus
            )
            LaporanMenuItem(
                icon        = Icons.Default.Description,
                iconBgColor = ExpenseRed.copy(alpha = 0.12f),
                iconColor   = ExpenseRed,
                title       = "Laporan Detail Keuangan",
                subtitle    = "Ringkasan lengkap pemasukan dan pengeluaran organisasi",
                onClick     = viewModel::onShowLaporanDetail
            )
        }
    }

    // ── Bottom Sheets ──────────────────────────────────────────
    // PERBAIKAN: Memanggil fungsi sheet tanpa argumen karena parameter tidak terdefinisi di tujuannya
    when (uiState.activeSheet) {
        LaporanType.IURAN_BULANAN   -> LaporanIuranBulananSheet(uiState = uiState, viewModel = viewModel)
        LaporanType.STATUS_BAYAR    -> LaporanStatusBayarSheet(uiState = uiState, viewModel = viewModel)
        LaporanType.DETAIL_KEUANGAN -> LaporanDetailKeuanganSheet(uiState = uiState, viewModel = viewModel)
        null -> Unit
    }
}

@Composable
private fun LaporanMenuItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextLight, maxLines = 2)
            }
            // PERBAIKAN: Mengubah dari Icons.AutoMirrored.Filled.ChevronRight ke Icons.Default.ChevronRight
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}