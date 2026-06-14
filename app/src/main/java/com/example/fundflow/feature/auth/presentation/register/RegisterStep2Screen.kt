package com.example.fundflow.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun RegisterStep2Screen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccessState()
            onRegisterSuccess()
        }
    }

    var showMulaiPicker   by remember { mutableStateOf(false) }
    var showSelesaiPicker by remember { mutableStateOf(false) }
    val currentYear       = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    val configuration = LocalConfiguration.current
    val currentLocale = remember(configuration) {
        ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
    }

    val monthOptions = remember(currentLocale) {
        val monthsList = (1..12).map { month ->
            val num  = month.toString().padStart(2, '0')
            val name = Month.of(month).getDisplayName(TextStyle.FULL, currentLocale)
                .replaceFirstChar { it.uppercase() }
            num to name
        }
        buildList {
            for (y in currentYear..(currentYear + 1)) {
                for ((num, name) in monthsList) {
                    add("$y-$num" to "$name $y")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Step Indicator (inline, Step 2 aktif) ─────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Step 1 — done
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(PrimaryLime, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", style = MaterialTheme.typography.labelMedium,
                        color = TextDark, fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(modifier = Modifier.width(32.dp), thickness = 2.dp, color = PrimaryLime)
                // Step 2 — active
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(PrimaryLime, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", style = MaterialTheme.typography.labelMedium,
                        color = TextDark, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text       = stringResource(R.string.register_step2_title),
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = stringResource(R.string.register_step2_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Nama Organisasi ───────────────────────────────
            FundFlowTextField(
                value         = uiState.namaOrganisasi,
                onValueChange = viewModel::onNamaOrganisasiChange,
                label         = stringResource(R.string.register_step2_label_org),
                leadingIcon   = Icons.Default.Business,
                isError       = uiState.namaOrganisasiError != null,
                errorMessage  = uiState.namaOrganisasiError
            )

            Spacer(Modifier.height(20.dp))

            // ── Periode Mulai ─────────────────────────────────
            Text(
                text       = stringResource(R.string.register_step2_label_start),
                style      = MaterialTheme.typography.labelLarge,
                color      = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))

            val mulaiLabel = monthOptions.find { it.first == uiState.periodeMulai }?.second
                ?: stringResource(R.string.register_step2_placeholder_start)

            OutlinedButton(
                onClick  = { showMulaiPicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.small,
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (uiState.periodeMulai.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(mulaiLabel, modifier = Modifier.weight(1f))
            }
            if (uiState.periodeMulaiError != null) {
                Text(
                    uiState.periodeMulaiError!!,
                    color    = ExpenseRed,
                    style    = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Periode Selesai ───────────────────────────────
            Text(
                text       = stringResource(R.string.register_step2_label_end),
                style      = MaterialTheme.typography.labelLarge,
                color      = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(6.dp))

            val selesaiLabel = monthOptions.find { it.first == uiState.periodeSelesai }?.second
                ?: stringResource(R.string.register_step2_placeholder_end)

            OutlinedButton(
                onClick  = { showSelesaiPicker = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = MaterialTheme.shapes.small,
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (uiState.periodeSelesai.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(selesaiLabel, modifier = Modifier.weight(1f))
            }
            if (uiState.periodeSelesaiError != null) {
                Text(
                    uiState.periodeSelesaiError!!,
                    color    = ExpenseRed,
                    style    = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            // Error umum
            if (uiState.errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.10f)),
                    shape    = MaterialTheme.shapes.small
                ) {
                    Text(
                        text     = uiState.errorMessage!!,
                        color    = ExpenseRed,   // tetap: warna semantik
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            FundFlowPrimaryButton(
                text      = stringResource(R.string.register_step2_btn_submit),
                onClick   = viewModel::register,
                isLoading = uiState.isLoading
            )

            Spacer(Modifier.height(32.dp))
        }
    }

    // ── Picker Bottom Sheet: Periode Mulai ────────────────────
    if (showMulaiPicker) {
        MonthPickerSheet(
            options     = monthOptions,
            selectedKey = uiState.periodeMulai,
            onSelect    = { key ->
                viewModel.onPeriodeMulaiChange(key)
                showMulaiPicker = false
            },
            onDismiss   = { showMulaiPicker = false }
        )
    }

    // ── Picker Bottom Sheet: Periode Selesai ──────────────────
    if (showSelesaiPicker) {
        MonthPickerSheet(
            options     = monthOptions,
            selectedKey = uiState.periodeSelesai,
            onSelect    = { key ->
                viewModel.onPeriodeSelesaiChange(key)
                showSelesaiPicker = false
            },
            onDismiss   = { showSelesaiPicker = false }
        )
    }
}

// ── Bottom Sheet picker bulan ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthPickerSheet(
    options: List<Pair<String, String>>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    FundFlowBottomSheet(onDismiss = onDismiss) {
        Text(
            text       = stringResource(R.string.register_step2_dialog_title),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            // FIX: reaktif terhadap tema
            color      = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(12.dp))
        options.forEach { (key, label) ->
            val isSelected = key == selectedKey
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                onClick  = { onSelect(key) },
                shape    = MaterialTheme.shapes.small,
                color    = if (isSelected) PrimaryLime.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text       = label,
                        style      = MaterialTheme.typography.bodyMedium,
                        color      = if (isSelected) PrimaryLimeDark
                        else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier   = Modifier.weight(1f)
                    )
                    if (isSelected) {
                        Text("✓", color = PrimaryLimeDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}