package com.example.fundflow.feature.laporan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

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
                        "Laporan",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        // FIX: reaktif terhadap tema
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // FIX: reaktif terhadap tema
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        // FIX: reaktif terhadap tema
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Export Laporan Header Card ─────────────────────
            // HeaderGreen tetap: warna brand. Hanya teks di dalamnya yang perlu reaktif.
            Card(
                modifier  = Modifier.fillMaxWidth(),
                colors    = CardDefaults.cardColors(containerColor = HeaderGreen.copy(alpha = 0.25f)),
                shape     = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(HeaderGreen),   // tetap: warna brand
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon di atas HeaderGreen → tetap pakai TextDark
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = TextDark)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Export Laporan",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            // FIX: reaktif terhadap tema
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Pilih jenis laporan untuk melihat preview dan export ke PDF atau Excel",
                            style = MaterialTheme.typography.bodySmall,
                            // FIX: reaktif terhadap tema
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Section Label ──────────────────────────────────
            Text(
                "JENIS LAPORAN",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                // FIX: reaktif terhadap tema
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier   = Modifier.padding(top = 8.dp, start = 4.dp)
            )

            // ── List Jenis Laporan ──────────────────────────────
            // iconBgColor & iconColor tetap: warna semantik per kategori laporan
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
        // FIX: reaktif terhadap tema
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(iconBgColor),   // tetap: warna semantik
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = iconColor,   // tetap: warna semantik
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    style    = MaterialTheme.typography.bodySmall,
                    // FIX: reaktif terhadap tema
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                // FIX: reaktif terhadap tema
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}