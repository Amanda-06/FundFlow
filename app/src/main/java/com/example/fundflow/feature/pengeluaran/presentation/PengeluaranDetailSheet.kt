package com.example.fundflow.feature.pengeluaran.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pemasukan.presentation.DatePickerField
import com.example.fundflow.feature.pemasukan.presentation.DropdownField
import com.example.fundflow.feature.pemasukan.presentation.LabelSection
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranDetailSheet(
    uiState: PengeluaranState,
    viewModel: PengeluaranViewModel
) {
    val isEdit = uiState.editTarget != null

    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text       = if (isEdit) "Edit Pengeluaran" else "Detail Pengeluaran",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark,
                modifier   = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::onDismissSheet) {
                Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextLight)
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Deskripsi ─────────────────────────────────────
            FundFlowTextField(
                value         = uiState.formDeskripsi,
                onValueChange = viewModel::onFormDeskripsiChange,
                label         = "Deskripsi",
                placeholder   = "Masukkan deskripsi pengeluaran",
                isError       = uiState.formDeskripsiError != null,
                errorMessage  = uiState.formDeskripsiError
            )

            // ── Kategori ──────────────────────────────────────
            DropdownField(
                label        = "Kategori",
                selectedItem = uiState.formKategori,
                items        = Pengeluaran.KATEGORI_OPTIONS,
                placeholder  = "Pilih kategori",
                onItemSelect = viewModel::onFormKategoriChange,
                isError      = uiState.formKategoriError != null,
                errorMessage = uiState.formKategoriError
            )

            // ── Nama Program (opsional) ───────────────────────
            FundFlowTextField(
                value         = uiState.formNamaProgram,
                onValueChange = viewModel::onFormNamaProgramChange,
                label         = "Nama Program Kerja (Opsional)",
                placeholder   = "Misal: Seminar Nasional IT"
            )

            // ── Qty + Harga Satuan ────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FundFlowTextField(
                    value           = uiState.formQuantity,
                    onValueChange   = viewModel::onFormQuantityChange,
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

            // ── Nominal (read-only) ───────────────────────────
            LabelSection(label = "Nominal") {
                Column {
                    Text(
                        CurrencyFormatter.format(uiState.formTotalNominal),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ExpenseRed
                    )
                    val qty   = uiState.formQuantity.toIntOrNull() ?: 1
                    val harga = uiState.formHargaSatuan.toDoubleOrNull() ?: 0.0
                    Text("$qty x ${CurrencyFormatter.format(harga)}", style = MaterialTheme.typography.bodySmall, color = TextLight)
                }
            }

            // ── Metode Pembayaran ─────────────────────────────
            DropdownField(
                label        = "Metode Pembayaran",
                selectedItem = uiState.formMetode,
                items        = Pengeluaran.METODE_OPTIONS,
                placeholder  = "Pilih metode",
                onItemSelect = viewModel::onFormMetodeChange,
                isError      = uiState.formMetodeError != null,
                errorMessage = uiState.formMetodeError
            )

            // ── Tanggal ───────────────────────────────────────
            DatePickerField(
                label        = "Tanggal",
                value        = uiState.formTanggal,
                onDateSelect = viewModel::onFormTanggalChange,
                isError      = uiState.formTanggalError != null,
                errorMessage = uiState.formTanggalError
            )

            // ── Catatan ───────────────────────────────────────
            FundFlowTextField(
                value         = uiState.formCatatan,
                onValueChange = viewModel::onFormCatatanChange,
                label         = "Catatan (Opsional)",
                placeholder   = "Tambahkan...",
                singleLine    = false,
                maxLines      = 3
            )

            Spacer(Modifier.height(8.dp))

            FundFlowPrimaryButton(
                text    = if (isEdit) "Simpan Perubahan" else "Tambah Pengeluaran",
                onClick = viewModel::onSave
            )
        }
    }
}