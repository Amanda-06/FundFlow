package com.example.fundflow.feature.anggota.presentation

import com.example.fundflow.feature.anggota.domain.model.Anggota

data class AnggotaState(
    val anggotaList: List<Anggota>  = emptyList(),
    val filteredList: List<Anggota> = emptyList(),
    val isLoading: Boolean          = false,
    val isSelectionMode: Boolean    = false,
    val selectedIds: Set<Int>       = emptySet(),
    val searchQuery: String         = "",

    // Form tambah/edit
    val showAddDialog: Boolean      = false,
    val editTarget: Anggota?        = null,
    val inputNama: String           = "",
    val inputNamaError: String?     = null,

    // Delete dialog
    val showDeleteDialog: Boolean   = false,
    val deleteTargetId: Int?        = null,

    val errorMessage: String?       = null
)
