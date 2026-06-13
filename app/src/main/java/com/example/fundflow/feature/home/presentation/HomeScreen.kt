// feature/home/presentation/HomeScreen.kt
// ============================================================
package com.example.fundflow.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.home.domain.model.DashboardSummary
import com.example.fundflow.feature.home.domain.model.Holiday
import com.example.fundflow.feature.home.domain.model.RecentTransaction
import com.example.fundflow.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToIuran: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val labelTransaksiTerakhir = stringResource(R.string.home_transaksi_terakhir)
    val labelBelumAdaTransaksi = stringResource(R.string.home_belum_ada_transaksi)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // REVISI: Menggunakan warna background yang dinamis
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // ── Top Bar ───────────────────────────────────────────
        item {
            HomeTopBar(
                userName        = uiState.userName.ifEmpty { "FundFlow" },
                onProfileClick  = onNavigateToProfile,
                onSettingsClick = onNavigateToSettings
            )
        }

        // ── Kartu Saldo Utama ─────────────────────────────────
        item {
            SaldoCard(
                summary  = uiState.summary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ── Status Iuran Bulan Ini ────────────────────────────
        item {
            IuranStatusCard(
                lunasCount      = uiState.summary.iuranSummary.lunasCount,
                belumBayarCount = uiState.summary.iuranSummary.belumBayarCount,
                onTagihClick    = onNavigateToIuran,
                modifier        = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // ── Hari Libur Terdekat (Nager.Date) ─────────────────
        item {
            when {
                uiState.isLoadingHolidays -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(24.dp),
                            color       = IuranBlue,
                            strokeWidth = 2.dp
                        )
                    }
                }
                uiState.upcomingHoliday != null -> {
                    HolidayCard(
                        holiday  = uiState.upcomingHoliday!!,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                uiState.holidayError != null -> {
                    // Gagal load API
                }
            }
        }

        // ── Label Transaksi Terakhir ──────────────────────────
        item {
            Text(
                text       = labelTransaksiTerakhir,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                // REVISI: Dinamis mengikuti tema
                color      = MaterialTheme.colorScheme.onBackground,
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // ── List Transaksi Terakhir ───────────────────────────
        if (uiState.isLoadingSummary) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryLime)
                }
            }
        } else if (uiState.summary.recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = labelBelumAdaTransaksi,
                        // REVISI: Dinamis mengikuti tema
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(uiState.summary.recentTransactions) { tx ->
                RecentTransactionItem(
                    transaction = tx,
                    modifier    = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── Top Bar ───────────────────────────────────────────────────
@Composable
private fun HomeTopBar(
    userName: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val greeting = stringResource(R.string.home_greeting, userName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // REVISI: Dinamis mengikuti tema
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "FundFlow",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // REVISI: Dinamis mengikuti tema
                    color      = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text  = greeting,
                style = MaterialTheme.typography.bodySmall,
                // REVISI: Dinamis mengikuti tema
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.settings_title),
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        // REVISI: Dinamis mengikuti tema
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile_title),
                        // REVISI: Dinamis mengikuti tema
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Kartu Saldo ───────────────────────────────────────────────
@Composable
private fun SaldoCard(
    summary: DashboardSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        // REVISI: Menggunakan primaryContainer agar menyesuaikan dark mode
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape     = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = stringResource(R.string.home_total_saldo),
                style = MaterialTheme.typography.bodyMedium,
                // REVISI: Dinamis mengikuti warna kontainer
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = CurrencyFormatter.format(summary.totalSaldo),
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = stringResource(R.string.home_pemasukan),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text       = "+ ${CurrencyFormatter.format(summary.totalPemasukan)}",
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = IncomeGreen // Teks aksen tetap brand color
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = stringResource(R.string.home_pengeluaran),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text       = "- ${CurrencyFormatter.format(summary.totalPengeluaran)}",
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = ExpenseRed // Teks aksen tetap brand color
                    )
                }
            }
        }
    }
}

// ── Status Iuran Bulan Ini ────────────────────────────────────
@Composable
private fun IuranStatusCard(
    lunasCount: Int,
    belumBayarCount: Int,
    onTagihClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        // REVISI: Dinamis menggunakan surface color
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape     = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = stringResource(R.string.home_status_iuran),
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    // REVISI: Dinamis
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = stringResource(
                        R.string.home_status_iuran_desc,
                        lunasCount,
                        lunasCount + belumBayarCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    // REVISI: Dinamis
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onTagihClick,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = PrimaryLime,
                    contentColor   = Color.Black // Teks di atas PrimaryLime bagusnya tetap gelap
                ),
                shape          = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    stringResource(R.string.home_tagih),
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Card Hari Libur (Nager.Date) ──────────────────────────────
@Composable
private fun HolidayCard(
    holiday: Holiday,
    modifier: Modifier = Modifier
) {
    val formattedDate = runCatching {
        val ld        = LocalDate.parse(holiday.date)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
        ld.format(formatter)
    }.getOrDefault(holiday.date)

    Card(
        modifier  = modifier,
        // REVISI: Menggunakan Surface agar lebih netral, dengan sedikit warna iuran blue
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape     = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(IuranBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎉", fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = stringResource(R.string.home_hari_libur_terdekat),
                    style      = MaterialTheme.typography.labelSmall,
                    color      = IuranBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text       = holiday.localName,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    // REVISI: Dinamis
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    // REVISI: Dinamis
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Item Transaksi Terakhir ───────────────────────────────────
@Composable
private fun RecentTransactionItem(
    transaction: RecentTransaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        // REVISI: Dinamis
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape     = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.isIncome) IncomeGreen.copy(alpha = 0.12f)
                        else ExpenseRed.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (transaction.isIncome) Icons.Default.TrendingUp
                    else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint               = if (transaction.isIncome) IncomeGreen else ExpenseRed,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = transaction.title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    // REVISI: Dinamis
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1
                )
                Text(
                    text     = transaction.subtitle,
                    style    = MaterialTheme.typography.bodySmall,
                    // REVISI: Dinamis
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text       = "${if (transaction.isIncome) "+" else "-"} ${CurrencyFormatter.format(transaction.amount)}",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = if (transaction.isIncome) IncomeGreen else ExpenseRed
            )
        }
    }
}