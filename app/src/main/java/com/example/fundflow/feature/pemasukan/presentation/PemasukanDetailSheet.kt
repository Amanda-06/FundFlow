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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
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

    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = if (isEdit) "Edit Pemasukan" else "Detail Pemasukan",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurface, // FIX: was TextDark
                modifier   = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::onDismissSheet) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
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
                label         = "Deskripsi",
                placeholder   = "Masukkan deskripsi pemasukan",
                isError       = uiState.formDeskripsiError != null,
                errorMessage  = uiState.formDeskripsiError
            )

            DropdownField(
                label        = "Sumber Pemasukan",
                selectedItem = uiState.formSumber,
                items        = Pemasukan.SUMBER_OPTIONS,
                placeholder  = "Pilih sumber (misal: Iuran Anggota)",
                onItemSelect = viewModel::onFormSumberChange,
                isError      = uiState.formSumberError != null,
                errorMessage = uiState.formSumberError
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FundFlowTextField(
                    value           = uiState.formQty,
                    onValueChange   = viewModel::onFormQtyChange,
                    label           = "Qty",
                    modifier        = Modifier.weight(0.4f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                FundFlowTextField(
                    value           = uiState.formHargaSatuan,
                    onValueChange   = viewModel::onFormHargaSatuanChange,
                    label           = "Harga Satuan",
                    placeholder     = "Rp 0",
                    modifier        = Modifier.weight(0.6f),
                    isError         = uiState.formHargaSatuanError != null,
                    errorMessage    = uiState.formHargaSatuanError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            LabelSection(label = "Total Nominal") {
                Column {
                    Text(
                        text       = CurrencyFormatter.format(uiState.formTotalNominal),
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = IncomeGreenDark // tetap — warna brand aksen
                    )
                    val qty   = uiState.formQty.toIntOrNull() ?: 1
                    val harga = uiState.formHargaSatuan.toDoubleOrNull() ?: 0.0
                    Text(
                        text  = "$qty x ${CurrencyFormatter.format(harga)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
                    )
                }
            }

            DropdownField(
                label        = "Metode Penerimaan",
                selectedItem = uiState.formMetode,
                items        = Pemasukan.METODE_OPTIONS,
                placeholder  = "Pilih metode (Cash/Transfer)",
                onItemSelect = viewModel::onFormMetodeChange,
                isError      = uiState.formMetodeError != null,
                errorMessage = uiState.formMetodeError
            )

            DatePickerField(
                label        = "Tanggal",
                value        = uiState.formTanggal,
                onDateSelect = viewModel::onFormTanggalChange,
                isError      = uiState.formTanggalError != null,
                errorMessage = uiState.formTanggalError
            )

            FundFlowTextField(
                value         = uiState.formCatatan,
                onValueChange = viewModel::onFormCatatanChange,
                label         = "Catatan (Opsional)",
                placeholder   = "Tambahkan catatan...",
                singleLine    = false,
                maxLines      = 3
            )

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                text    = if (isEdit) "Simpan Perubahan" else "Simpan Pemasukan",
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextMuted
                    )
                },
                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                isError       = isError,
                modifier      = Modifier.fillMaxWidth().menuAnchor(),
                shape         = MaterialTheme.shapes.small,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryLimeDark,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline, // FIX: was BorderGray
                    errorBorderColor     = ExpenseRed,
                    focusedLabelColor    = PrimaryLimeDark,
                    unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
                )
            )
            ExposedDropdownMenu(
                expanded         = expanded,
                onDismissRequest = { expanded = false },
                modifier         = Modifier.background(MaterialTheme.colorScheme.surface) // FIX: was CardWhite
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text    = {
                            Text(
                                item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface // FIX: was TextDark
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
            color      = MaterialTheme.colorScheme.onSurface, // FIX: was TextDark
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

    val displayValue = runCatching {
        val ld = LocalDate.parse(value)
        ld.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID")))
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
                        contentDescription = "Pilih tanggal",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant // FIX: was TextLight
                    )
                }
            },
            isError  = isError,
            modifier = Modifier.fillMaxWidth(),
            shape    = MaterialTheme.shapes.small,
            colors   = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryLimeDark,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,      // FIX: was BorderGray
                errorBorderColor     = ExpenseRed,
                focusedLabelColor    = PrimaryLimeDark,
                unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant, // FIX: was TextLight
                focusedTextColor     = MaterialTheme.colorScheme.onSurface,    // FIX: was TextDark
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface     // FIX: was TextDark
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
                }) { Text("OK", color = PrimaryLimeDark) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant) // FIX: was TextLight
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