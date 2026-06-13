// feature/pengeluaran/presentation/PengeluaranDetailSheet.kt
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fundflow.R
import com.example.fundflow.core.util.CurrencyFormatter
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

    // FIX: Mengambil list opsi langsung dari resource string array agar mendukung multi-bahasa
    val kategoriOptions = stringArrayResource(R.array.pengeluaran_kategori_options).toList()
    val metodeOptions   = stringArrayResource(R.array.pengeluaran_metode_options).toList()

    FundFlowBottomSheet(onDismiss = viewModel::onDismissSheet) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text       = if (isEdit) stringResource(R.string.pengeluaran_edit_title)
                else        stringResource(R.string.pengeluaran_detail_title),
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
                label         = stringResource(R.string.pengeluaran_deskripsi),
                placeholder   = stringResource(R.string.pengeluaran_deskripsi_hint),
                isError       = uiState.formDeskripsiError != null,
                errorMessage  = uiState.formDeskripsiError
            )

            DropdownField(
                label        = stringResource(R.string.pengeluaran_kategori),
                selectedItem = uiState.formKategori,
                items        = kategoriOptions, // FIX: Menggunakan opsi multi-bahasa
                placeholder  = stringResource(R.string.pengeluaran_kategori_hint),
                onItemSelect = viewModel::onFormKategoriChange,
                isError      = uiState.formKategoriError != null,
                errorMessage = uiState.formKategoriError
            )

            FundFlowTextField(
                value         = uiState.formNamaProgram,
                onValueChange = viewModel::onFormNamaProgramChange,
                label         = stringResource(R.string.pengeluaran_nama_program),
                placeholder   = stringResource(R.string.pengeluaran_nama_program_hint)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FundFlowTextField(
                    value           = uiState.formQuantity,
                    onValueChange   = viewModel::onFormQuantityChange,
                    label           = stringResource(R.string.pemasukan_qty),
                    modifier        = Modifier.weight(0.4f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                FundFlowTextField(
                    value           = uiState.formHargaSatuan,
                    onValueChange   = viewModel::onFormHargaSatuanChange,
                    label           = stringResource(R.string.pemasukan_harga_satuan),
                    placeholder     = stringResource(R.string.iuran_nominal_hint),
                    modifier        = Modifier.weight(0.6f),
                    isError         = uiState.formHargaSatuanError != null,
                    errorMessage    = uiState.formHargaSatuanError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            LabelSection(label = stringResource(R.string.pengeluaran_nominal)) {
                Column {
                    Text(
                        CurrencyFormatter.format(uiState.formTotalNominal),
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = ExpenseRed // tetap — warna brand aksen pengeluaran
                    )
                    val qty   = uiState.formQuantity.toIntOrNull() ?: 1
                    val harga = uiState.formHargaSatuan.toDoubleOrNull() ?: 0.0
                    Text(
                        "$qty x ${CurrencyFormatter.format(harga)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownField(
                label        = stringResource(R.string.pengeluaran_metode_pembayaran),
                selectedItem = uiState.formMetode,
                items        = metodeOptions, // FIX: Menggunakan opsi multi-bahasa
                placeholder  = stringResource(R.string.iuran_metode_hint),
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
                text    = if (isEdit) stringResource(R.string.pengeluaran_simpan_perubahan)
                else        stringResource(R.string.pengeluaran_tambah),
                onClick = viewModel::onSave
            )
        }
    }
}