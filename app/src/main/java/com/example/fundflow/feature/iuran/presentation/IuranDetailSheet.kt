package com.example.fundflow.feature.iuran.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fundflow.R
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.pemasukan.presentation.DatePickerField
import com.example.fundflow.feature.pemasukan.presentation.DropdownField
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IuranDetailSheet(
    uiState: IuranState,
    viewModel: IuranViewModel
) {
    val iuran = uiState.selectedIuran ?: return

    FundFlowBottomSheet(onDismiss = viewModel::onDismissDetailSheet) {
        Text(
            text       = stringResource(R.string.iuran_detail_title),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        FundFlowSurfaceCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.inverseSurface,
                            MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.inverseOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    iuran.namaAnggota,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                stringResource(R.string.iuran_status_pembayaran),
                style      = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface
            )

            FundFlowSurfaceCard {
                Column {
                    SwitchRow(
                        label           = stringResource(R.string.iuran_sudah_bayar),
                        checked         = uiState.formStatusBayar,
                        onCheckedChange = viewModel::onToggleStatusBayar
                    )
                    Spacer(Modifier.height(4.dp))
                    SwitchRow(
                        label           = stringResource(R.string.iuran_terlambat),
                        checked         = uiState.formTerlambat,
                        enabled         = uiState.formStatusBayar,
                        onCheckedChange = viewModel::onToggleTerlambat
                    )
                }
            }

            if (uiState.formStatusBayar) {
                Text(
                    stringResource(R.string.iuran_detail_pembayaran),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface
                )

                FundFlowTextField(
                    value           = uiState.formNominal,
                    onValueChange   = viewModel::onFormNominalChange,
                    label           = stringResource(R.string.iuran_nominal),
                    placeholder     = stringResource(R.string.iuran_nominal_hint),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                DropdownField(
                    label        = stringResource(R.string.iuran_metode_pembayaran),
                    selectedItem = uiState.formMetode,
                    items        = Iuran.METODE_OPTIONS,
                    placeholder  = stringResource(R.string.iuran_metode_hint),
                    onItemSelect = viewModel::onFormMetodeChange
                )

                DatePickerField(
                    label        = stringResource(R.string.iuran_tanggal_pembayaran),
                    value        = uiState.formTanggalBayar,
                    onDateSelect = viewModel::onFormTanggalBayarChange
                )
            }

            FundFlowTextField(
                value         = uiState.formCatatan,
                onValueChange = viewModel::onFormCatatanChange,
                label         = stringResource(R.string.iuran_catatan_opsional),
                placeholder   = stringResource(R.string.pemasukan_catatan_hint),
                singleLine    = false,
                maxLines      = 3
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FundFlowSecondaryButton(
                    text     = stringResource(R.string.iuran_batal),
                    onClick  = viewModel::onDismissDetailSheet,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick  = viewModel::onSaveDetail,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLime, // tetap — warna brand
                        contentColor   = TextDark
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.iuran_simpan), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            enabled         = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = MaterialTheme.colorScheme.surface,
                checkedTrackColor   = PrimaryLime,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}