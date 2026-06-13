// feature/pemasukan/presentation/PemasukanDetailSheet.kt
// ============================================================
package com.example.fundflow.feature.pemasukan.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemasukanDetailSheet(
    uiState: PemasukanState,
    viewModel: PemasukanViewModel
) {
    val isEdit = uiState.editTarget != null

    // Mengambil list opsi langsung dari resource string array agar mendukung multi-bahasa
    val sumberOptions = stringArrayResource(R.array.pemasukan_sumber_options).toList()
    val metodeOptions = stringArrayResource(R.array.pemasukan_metode_options).toList()

    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = if (isEdit) stringResource(R.string.pemasukan_edit_title)
                else        stringResource(R.string.pemasukan_detail_title),
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface,
                modifier   = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::onDismissSheet) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.common_close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FundFlowTextField(
                value         = uiState.formDeskripsi,
                onValueChange = viewModel::onFormDeskripsiChange,
                label         = stringResource(R.string.pemasukan_deskripsi),
                placeholder   = stringResource(R.string.pemasukan_deskripsi_hint),
                isError       = uiState.formDeskripsiError != null,
                errorMessage  = uiState.formDeskripsiError
            )

            DropdownField(
                label        = stringResource(R.string.pemasukan_sumber),
                selectedItem = uiState.formSumber,
                items        = sumberOptions,
                placeholder  = stringResource(R.string.pemasukan_sumber_hint),
                onItemSelect = viewModel::onFormSumberChange,
                isError      = uiState.formSumberError != null,
                errorMessage = uiState.formSumberError
            )

            // REVISI: Qty dan Harga Satuan dihapus, diganti langsung dengan input Total Nominal
            FundFlowTextField(
                value           = uiState.formNominal,
                onValueChange   = viewModel::onFormNominalChange,
                label           = stringResource(R.string.pemasukan_total_nominal),
                placeholder     = stringResource(R.string.iuran_nominal_hint),
                isError         = uiState.formNominalError != null,
                errorMessage    = uiState.formNominalError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            DropdownField(
                label        = stringResource(R.string.pemasukan_metode_penerimaan),
                selectedItem = uiState.formMetode,
                items        = metodeOptions,
                placeholder  = stringResource(R.string.pemasukan_metode_hint),
                onItemSelect = viewModel::onFormMetodeChange,
                isError      = uiState.formMetodeError != null,
                errorMessage = uiState.formMetodeError
            )

            DatePickerField(
                label        = stringResource(R.string.pemasukan_tanggal),
                value        = uiState.formTanggal,
                onDateSelect = viewModel::onFormTanggalChange,
                isError      = uiState.formTanggalError != null,
                errorMessage = uiState.formTanggalError
            )

            FundFlowTextField(
                value         = uiState.formCatatan,
                onValueChange = viewModel::onFormCatatanChange,
                label         = stringResource(R.string.iuran_catatan_opsional),
                placeholder   = stringResource(R.string.pemasukan_catatan_hint),
                singleLine    = false,
                maxLines      = 3
            )

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                text    = if (isEdit) stringResource(R.string.pemasukan_simpan_perubahan)
                else        stringResource(R.string.pemasukan_simpan),
                onClick = viewModel::onSavePemasukan
            )
        }
    }
}

// ── Helper composables (shared, dipakai di PengeluaranDetailSheet juga) ──

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedItem: String,
    items: List<String>,
    placeholder: String,
    onItemSelect: (String) -> Unit,
    isError: Boolean      = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ExposedDropdownMenuBox(
            expanded         = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value         = selectedItem,
                onValueChange = {},
                readOnly      = true,
                label         = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                placeholder   = {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                isError       = isError,
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = MaterialTheme.shapes.small,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryLimeDark,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor     = ExpenseRed,
                    focusedLabelColor    = PrimaryLimeDark,
                    unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false },
                modifier         = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text    = {
                            Text(
                                item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { onItemSelect(item); expanded = false }
                    )
                }
            }
        }
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                color    = ExpenseRed,
                style    = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun LabelSection(label: String, content: @Composable () -> Unit) {
    Column {
        Text(
            label,
            style      = MaterialTheme.typography.labelLarge,
            color      = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelect: (String) -> Unit,
    isError: Boolean      = false,
    errorMessage: String? = null
) {
    var showPicker by remember { mutableStateOf(false) }

    // Locale dinamis agar format nama bulan ikut berubah otomatis
    val configuration = LocalConfiguration.current
    val currentLocale = remember(configuration) {
        ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()
    }

    val displayValue = runCatching {
        val ld = LocalDate.parse(value)
        ld.format(DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale))
    }.getOrDefault(value)

    Column {
        OutlinedTextField(
            value         = displayValue,
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon  = {
                IconButton(onClick = { showPicker = true }) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.common_pilih_tanggal),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            isError  = isError,
            modifier = Modifier.fillMaxWidth(),
            shape    = MaterialTheme.shapes.small,
            colors   = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryLimeDark,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor     = ExpenseRed,
                focusedLabelColor    = PrimaryLimeDark,
                unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
            )
        )
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                color    = ExpenseRed,
                style    = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }

    if (showPicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = runCatching {
                LocalDate.parse(value).toEpochDay() * 86_400_000L
            }.getOrDefault(System.currentTimeMillis())
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let { millis ->
                        val ld = LocalDate.ofEpochDay(millis / 86_400_000L)
                        onDateSelect(ld.toString())
                    }
                    showPicker = false
                }) { Text(stringResource(R.string.common_ok), color = PrimaryLimeDark) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.common_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state  = dpState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = PrimaryLime,
                    selectedDayContentColor   = TextDark,
                    todayDateBorderColor      = PrimaryLimeDark
                )
            )
        }
    }
}