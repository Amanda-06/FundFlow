package com.example.fundflow.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.components.FundFlowPrimaryButton
import com.example.fundflow.ui.components.FundFlowTopBar
import com.example.fundflow.ui.theme.*
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

private val MONTHS = (1..12).map { month ->
    val num  = month.toString().padStart(2, '0')
    val name = Month.of(month).getDisplayName(TextStyle.FULL, Locale("id", "ID")).replaceFirstChar { it.uppercase() }
    num to name
}

@Composable
fun PengaturanPeriodeScreen(
    onNavigateBack: () -> Unit,
    viewModel: PengaturanPeriodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarState.showSnackbar("Periode kas berhasil diperbarui")
            viewModel.resetSuccess()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarState.showSnackbar(it); viewModel.clearError() }
    }

    var showMulaiPicker   by remember { mutableStateOf(false) }
    var showSelesaiPicker by remember { mutableStateOf(false) }
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    val monthOptions = buildList {
        for (y in (currentYear - 1)..(currentYear + 2)) {
            for ((num, name) in MONTHS) add("$y-$num" to "$name $y")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        topBar = { FundFlowTopBar(title = "Pengaturan Periode", onNavigateBack = onNavigateBack) },
        containerColor = AppBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLime)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Info Card ───────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = IuranBlue.copy(alpha = 0.10f)),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = IuranBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Mengubah periode kas akan memengaruhi rentang bulan yang ditampilkan pada halaman Iuran dan Laporan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark
                    )
                }
            }

            // ── Periode Mulai ─────────────────────────────────
            Text("Periode Bulan Mulai", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = TextDark)
            val mulaiLabel = monthOptions.find { it.first == uiState.bulanMulai }?.second ?: "Pilih Bulan"
            OutlinedButton(
                onClick  = { showMulaiPicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextLight)
                Spacer(Modifier.width(8.dp))
                Text(mulaiLabel, modifier = Modifier.weight(1f), color = TextDark)
            }
            if (uiState.bulanMulaiError != null) {
                Text(uiState.bulanMulaiError!!, color = ExpenseRed, style = MaterialTheme.typography.labelSmall)
            }

            // ── Periode Selesai ───────────────────────────────
            Text("Periode Bulan Berakhir", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = TextDark)
            val selesaiLabel = monthOptions.find { it.first == uiState.bulanSelesai }?.second ?: "Pilih Bulan"
            OutlinedButton(
                onClick  = { showSelesaiPicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TextLight)
                Spacer(Modifier.width(8.dp))
                Text(selesaiLabel, modifier = Modifier.weight(1f), color = TextDark)
            }
            if (uiState.bulanSelesaiError != null) {
                Text(uiState.bulanSelesaiError!!, color = ExpenseRed, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                text      = "Simpan Periode",
                onClick   = viewModel::onSave,
                isLoading = uiState.isSaving
            )
        }
    }

    if (showMulaiPicker) {
        MonthPickerDialog(monthOptions, uiState.bulanMulai, { viewModel.onBulanMulaiChange(it); showMulaiPicker = false }, { showMulaiPicker = false })
    }
    if (showSelesaiPicker) {
        MonthPickerDialog(monthOptions, uiState.bulanSelesai, { viewModel.onBulanSelesaiChange(it); showSelesaiPicker = false }, { showSelesaiPicker = false })
    }
}

@Composable
private fun MonthPickerDialog(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Bulan", style = MaterialTheme.typography.titleLarge, color = TextDark) },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                androidx.compose.foundation.lazy.LazyColumn {
                    items(options.size) { i ->
                        val (key, label) = options[i]
                        val isSelected = key == selected
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            onClick  = { onSelect(key) },
                            shape    = MaterialTheme.shapes.small,
                            color    = if (isSelected) PrimaryLime.copy(alpha = 0.2f) else CardWhite
                        ) {
                            Text(
                                label,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                color = if (isSelected) PrimaryLimeDark else TextDark,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tutup", color = TextDark) }
        },
        containerColor = CardWhite
    )
}

// Helper import untuk LazyColumn items pada AlertDialog
private fun <T> items(count: Int, itemContent: @Composable (Int) -> Unit) {}