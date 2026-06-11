package com.example.fundflow.feature.pengeluaran.presentation

import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran

data class PengeluaranState(
    val pengeluaranList: List<Pengeluaran> = emptyList(),
    val filteredList: List<Pengeluaran>    = emptyList(),
    val totalPengeluaran: Double           = 0.0,
    val isLoading: Boolean                 = true,
    val searchQuery: String                = "",
    val isSelectionMode: Boolean           = false,
    val selectedIds: Set<Int>              = emptySet(),
    val showFormSheet: Boolean             = false,
    val editTarget: Pengeluaran?           = null,

    // Form fields
    val formDeskripsi: String              = "",
    val formKategori: String               = "",
    val formNamaProgram: String            = "",
    val formMetode: String                 = "",
    val formQuantity: String               = "1",
    val formHargaSatuan: String            = "",
    val formTanggal: String                = "",
    val formCatatan: String                = "",

    // Form errors
    val formDeskripsiError: String?        = null,
    val formKategoriError: String?         = null,
    val formMetodeError: String?           = null,
    val formHargaSatuanError: String?      = null,
    val formTanggalError: String?          = null,

    val showDeleteDialog: Boolean          = false,
    val deleteTargetId: Int?               = null,
    val errorMessage: String?              = null,
    val successMessage: String?            = null
) {
    val formTotalNominal: Double
        get() {
            val qty   = formQuantity.toIntOrNull() ?: 1
            val harga = formHargaSatuan.toDoubleOrNull() ?: 0.0
            return qty * harga
        }
}