// ============================================================
// feature/iuran/presentation/IuranDetailSheet.kt
// ============================================================
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
            text       = "Detail Iuran",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color      = TextDark
        )

        Spacer(Modifier.height(16.dp))

        // ── Info Anggota ──────────────────────────────────────
        FundFlowSurfaceCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(NavBackground, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = CardWhite, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    iuran.namaAnggota,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextDark
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Status Pembayaran ─────────────────────────────
            Text("Status Pembayaran", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = TextDark)

            FundFlowSurfaceCard {
                Column {
                    SwitchRow(
                        label   = "Sudah Bayar",
                        checked = uiState.formStatusBayar,
                        onCheckedChange = viewModel::onToggleStatusBayar
                    )
                    Spacer(Modifier.height(4.dp))
                    SwitchRow(
                        label   = "Terlambat (Denda)",
                        checked = uiState.formTerlambat,
                        enabled = uiState.formStatusBayar,
                        onCheckedChange = viewModel::onToggleTerlambat
                    )
                }
            }

            // ── Detail Pembayaran (hanya jika sudah bayar) ────
            if (uiState.formStatusBayar) {
                Text("Detail Pembayaran", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium, color = TextDark)

                FundFlowTextField(
                    value         = uiState.formNominal,
                    onValueChange = viewModel::onFormNominalChange,
                    label         = "Nominal",
                    placeholder   = "Rp 0",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                DropdownField(
                    label        = "Metode Pembayaran",
                    selectedItem = uiState.formMetode,
                    items        = Iuran.METODE_OPTIONS,
                    placeholder  = "Pilih metode",
                    onItemSelect = viewModel::onFormMetodeChange
                )

                DatePickerField(
                    label        = "Tanggal Pembayaran",
                    value        = uiState.formTanggalBayar,
                    onDateSelect = viewModel::onFormTanggalBayarChange
                )
            }

            // ── Catatan ───────────────────────────────────────
            FundFlowTextField(
                value         = uiState.formCatatan,
                onValueChange = viewModel::onFormCatatanChange,
                label         = "Catatan (Opsional)",
                placeholder   = "Tambahkan catatan...",
                singleLine    = false,
                maxLines      = 3
            )

            Spacer(Modifier.height(8.dp))

            // ── Tombol Simpan / Batal ─────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FundFlowSecondaryButton(
                    text     = "Batal",
                    onClick  = viewModel::onDismissDetailSheet,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick  = viewModel::onSaveDetail,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = PrimaryLime, contentColor = TextDark),
                    shape    = MaterialTheme.shapes.medium
                ) {
                    Text("Simpan", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Helper: Switch row ────────────────────────────────────────
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
            color = if (enabled) TextDark else TextMuted
        )
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            enabled         = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = PrimaryLime,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = BorderGray
            )
        )
    }
}