package com.example.fundflow.feature.pemasukan.presentation

import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan

data class PemasukanState(
    val pemasukanList: List<Pemasukan>  = emptyList(),
    val filteredList: List<Pemasukan>   = emptyList(),
    val totalPemasukan: Double          = 0.0,
    val isLoading: Boolean              = true,
    val searchQuery: String             = "",

    // Multi-select
    val isSelectionMode: Boolean        = false,
    val selectedIds: Set<Int>           = emptySet(),

    // Bottom sheet form
    val showFormSheet: Boolean          = false,
    val editTarget: Pemasukan?          = null,

    // Form fields
    val formDeskripsi: String           = "",
    val formSumber: String              = "",
    val formMetode: String              = "",
    val formQty: String                 = "1",
    val formHargaSatuan: String         = "",
    val formTanggal: String             = "",
    val formCatatan: String             = "",

    // Form errors
    val formDeskripsiError: String?     = null,
    val formSumberError: String?        = null,
    val formMetodeError: String?        = null,
    val formHargaSatuanError: String?   = null,
    val formTanggalError: String?       = null,

    // Delete dialog
    val showDeleteDialog: Boolean       = false,
    val deleteTargetId: Int?            = null,

    val errorMessage: String?           = null,
    val successMessage: String?         = null
) {
    // Computed: total nominal dari form (qty × hargaSatuan)
    val formTotalNominal: Double
        get() {
            val qty    = formQty.toIntOrNull() ?: 1
            val harga  = formHargaSatuan.replace(",", "").replace(".", "").toDoubleOrNull() ?: 0.0
            return qty * harga
        }
}