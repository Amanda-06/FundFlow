
package com.example.fundflow.feature.pemasukan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.util.DateFormatter
import com.example.fundflow.feature.pemasukan.domain.model.Pemasukan
import com.example.fundflow.feature.pemasukan.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PemasukanViewModel @Inject constructor(
    private val getPemasukanList: GetPemasukanListUseCase,
    private val addPemasukan: AddPemasukanUseCase,
    private val updatePemasukan: UpdatePemasukanUseCase,
    private val deletePemasukan: DeletePemasukanUseCase,
    private val deleteSelected: DeleteSelectedPemasukanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PemasukanState())
    val uiState: StateFlow<PemasukanState> = _uiState.asStateFlow()

    init { observePemasukan() }

    private fun observePemasukan() {
        getPemasukanList()
            .onEach { list ->
                _uiState.update { state ->
                    state.copy(
                        pemasukanList  = list,
                        filteredList   = applyFilter(list, state.searchQuery),
                        totalPemasukan = list.sumOf { it.nominal },
                        isLoading      = false
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            .launchIn(viewModelScope)
    }

    // ── Search ────────────────────────────────────────────────
    fun onSearchChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery  = query,
                filteredList = applyFilter(state.pemasukanList, query)
            )
        }
    }

    private fun applyFilter(list: List<Pemasukan>, query: String) =
        if (query.isBlank()) list
        else list.filter {
            it.deskripsi.contains(query, true) || it.sumber.contains(query, true)
        }

    // ── Selection Mode ────────────────────────────────────────
    fun onItemLongClick(id: Int) =
        _uiState.update { it.copy(isSelectionMode = true, selectedIds = setOf(id)) }

    fun onItemClick(id: Int) {
        val state = _uiState.value
        if (!state.isSelectionMode) return
        val newIds = if (id in state.selectedIds) state.selectedIds - id else state.selectedIds + id
        _uiState.update { it.copy(selectedIds = newIds, isSelectionMode = newIds.isNotEmpty()) }
    }

    fun onSelectAll() {
        _uiState.update { it.copy(selectedIds = it.filteredList.map { p -> p.pemasukanId }.toSet()) }
    }

    fun onCancelSelection() =
        _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) }

    // ── Form sheet ────────────────────────────────────────────
    fun onShowAddSheet() {
        _uiState.update {
            it.copy(
                showFormSheet        = true,
                editTarget           = null,
                formDeskripsi        = "",
                formSumber           = "",
                formMetode           = "",
                formQty              = "1",
                formHargaSatuan      = "",
                formTanggal          = DateFormatter.formatStorage(DateFormatter.today()),
                formCatatan          = "",
                formDeskripsiError   = null,
                formSumberError      = null,
                formMetodeError      = null,
                formHargaSatuanError = null,
                formTanggalError     = null
            )
        }
    }

    fun onShowEditSheet(pemasukan: Pemasukan) {
        _uiState.update {
            it.copy(
                showFormSheet   = true,
                editTarget      = pemasukan,
                formDeskripsi   = pemasukan.deskripsi,
                formSumber      = pemasukan.sumber,
                formMetode      = pemasukan.metode,
                formQty         = pemasukan.qty.toString(),
                formHargaSatuan = pemasukan.hargaSatuan.toLong().toString(),
                formTanggal     = pemasukan.tanggal,
                formCatatan     = pemasukan.catatan
            )
        }
    }

    fun onDismissSheet() = _uiState.update { it.copy(showFormSheet = false, editTarget = null) }

    // Form field setters
    fun onFormDeskripsiChange(v: String)    = _uiState.update { it.copy(formDeskripsi = v, formDeskripsiError = null) }
    fun onFormSumberChange(v: String)       = _uiState.update { it.copy(formSumber = v, formSumberError = null) }
    fun onFormMetodeChange(v: String)       = _uiState.update { it.copy(formMetode = v, formMetodeError = null) }
    fun onFormQtyChange(v: String)          = _uiState.update { it.copy(formQty = v) }
    fun onFormHargaSatuanChange(v: String)  = _uiState.update { it.copy(formHargaSatuan = v, formHargaSatuanError = null) }
    fun onFormTanggalChange(v: String)      = _uiState.update { it.copy(formTanggal = v, formTanggalError = null) }
    fun onFormCatatanChange(v: String)      = _uiState.update { it.copy(formCatatan = v) }

    fun onSavePemasukan() {
        val s = _uiState.value
        // Validasi
        val deskErr  = if (s.formDeskripsi.isBlank()) "Deskripsi tidak boleh kosong" else null
        val sumberErr = if (s.formSumber.isBlank()) "Sumber dana harus dipilih" else null
        val metodeErr = if (s.formMetode.isBlank()) "Metode pembayaran harus dipilih" else null
        val hargaErr  = if (s.formHargaSatuan.isBlank()) "Harga satuan tidak boleh kosong"
        else if ((s.formHargaSatuan.toDoubleOrNull() ?: 0.0) <= 0.0) "Harga satuan harus lebih dari 0"
        else null
        val tglErr    = if (s.formTanggal.isBlank()) "Tanggal tidak boleh kosong" else null

        if (listOf(deskErr, sumberErr, metodeErr, hargaErr, tglErr).any { it != null }) {
            _uiState.update {
                it.copy(
                    formDeskripsiError   = deskErr,
                    formSumberError      = sumberErr,
                    formMetodeError      = metodeErr,
                    formHargaSatuanError = hargaErr,
                    formTanggalError     = tglErr
                )
            }
            return
        }

        val qty    = s.formQty.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val harga  = s.formHargaSatuan.toDoubleOrNull() ?: 0.0
        val nominal = qty * harga

        viewModelScope.launch {
            try {
                if (s.editTarget != null) {
                    updatePemasukan(
                        s.editTarget.copy(
                            deskripsi   = s.formDeskripsi.trim(),
                            sumber      = s.formSumber,
                            metode      = s.formMetode,
                            qty         = qty,
                            hargaSatuan = harga,
                            nominal     = nominal,
                            tanggal     = s.formTanggal,
                            catatan     = s.formCatatan.trim()
                        )
                    )
                } else {
                    addPemasukan(
                        Pemasukan(
                            deskripsi   = s.formDeskripsi.trim(),
                            sumber      = s.formSumber,
                            metode      = s.formMetode,
                            qty         = qty,
                            hargaSatuan = harga,
                            nominal     = nominal,
                            tanggal     = s.formTanggal,
                            catatan     = s.formCatatan.trim()
                        )
                    )
                }
                _uiState.update { it.copy(showFormSheet = false, editTarget = null, successMessage = "Pemasukan berhasil disimpan") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // ── Delete ────────────────────────────────────────────────
    fun onRequestDelete(id: Int)       = _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = id) }
    fun onRequestBatchDelete()         = _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = null) }
    fun onDismissDeleteDialog()        = _uiState.update { it.copy(showDeleteDialog = false, deleteTargetId = null) }

    fun onConfirmDelete() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                if (state.deleteTargetId != null) deletePemasukan(state.deleteTargetId)
                else deleteSelected(state.selectedIds.toList())
                _uiState.update { it.copy(showDeleteDialog = false, deleteTargetId = null, isSelectionMode = false, selectedIds = emptySet()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(showDeleteDialog = false, errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}