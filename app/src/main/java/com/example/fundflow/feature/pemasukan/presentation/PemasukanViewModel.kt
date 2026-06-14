package com.example.fundflow.feature.pemasukan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.util.DateFormatter
import com.example.fundflow.feature.pemasukan.data.repository.PemasukanRepositoryImpl // TAMBAHAN IMPORT
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
    private val deleteSelected: DeleteSelectedPemasukanUseCase,
    private val repository: PemasukanRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(PemasukanState())
    val uiState: StateFlow<PemasukanState> = _uiState.asStateFlow()

    init {
        observePemasukan()
        fetchDataDariCloud()
    }

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

    // Fungsi Tambahan untuk Memicu Sinkronisasi Background
    private fun fetchDataDariCloud() {
        viewModelScope.launch {
            try {
                repository.syncWithCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ── Search & Selection Mode ────────────────
    fun onSearchChange(query: String) = _uiState.update { state -> state.copy(searchQuery = query, filteredList = applyFilter(state.pemasukanList, query)) }
    private fun applyFilter(list: List<Pemasukan>, query: String) = if (query.isBlank()) list else list.filter { it.deskripsi.contains(query, true) || it.sumber.contains(query, true) }
    fun onItemLongClick(id: Int) = _uiState.update { it.copy(isSelectionMode = true, selectedIds = setOf(id)) }
    fun onItemClick(id: Int) {
        val state = _uiState.value
        if (!state.isSelectionMode) return
        val newIds = if (id in state.selectedIds) state.selectedIds - id else state.selectedIds + id
        _uiState.update { it.copy(selectedIds = newIds, isSelectionMode = newIds.isNotEmpty()) }
    }
    fun onSelectAll() = _uiState.update { it.copy(selectedIds = it.filteredList.map { p -> p.pemasukanId }.toSet()) }
    fun onCancelSelection() = _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) }

    // ── Form sheet ────────────────────────────────────────────
    fun onShowAddSheet() {
        _uiState.update {
            it.copy(
                showFormSheet      = true,
                editTarget         = null,
                formDeskripsi      = "",
                formSumber         = "",
                formMetode         = "",
                formNominal        = "",
                formTanggal        = DateFormatter.formatStorage(DateFormatter.today()),
                formCatatan        = "",
                formDeskripsiError = null,
                formSumberError    = null,
                formMetodeError    = null,
                formNominalError   = null,
                formTanggalError   = null
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
                formNominal     = pemasukan.nominal.toLong().toString(),
                formTanggal     = pemasukan.tanggal,
                formCatatan     = pemasukan.catatan
            )
        }
    }

    fun onDismissSheet() = _uiState.update { it.copy(showFormSheet = false, editTarget = null) }

    // Form field setters
    fun onFormDeskripsiChange(v: String) = _uiState.update { it.copy(formDeskripsi = v, formDeskripsiError = null) }
    fun onFormSumberChange(v: String)    = _uiState.update { it.copy(formSumber = v, formSumberError = null) }
    fun onFormMetodeChange(v: String)    = _uiState.update { it.copy(formMetode = v, formMetodeError = null) }
    fun onFormNominalChange(v: String)   = _uiState.update { it.copy(formNominal = v, formNominalError = null) }
    fun onFormTanggalChange(v: String)   = _uiState.update { it.copy(formTanggal = v, formTanggalError = null) }
    fun onFormCatatanChange(v: String)   = _uiState.update { it.copy(formCatatan = v) }

    fun onSavePemasukan() {
        val s = _uiState.value
        // Validasi
        val deskErr  = if (s.formDeskripsi.isBlank()) "Deskripsi tidak boleh kosong" else null
        val sumberErr = if (s.formSumber.isBlank()) "Sumber dana harus dipilih" else null
        val metodeErr = if (s.formMetode.isBlank()) "Metode pembayaran harus dipilih" else null
        val nominalErr = if (s.formNominal.isBlank()) "Total nominal tidak boleh kosong"
        else if ((s.formNominal.toDoubleOrNull() ?: 0.0) <= 0.0) "Total nominal harus lebih dari 0"
        else null
        val tglErr    = if (s.formTanggal.isBlank()) "Tanggal tidak boleh kosong" else null

        if (listOf(deskErr, sumberErr, metodeErr, nominalErr, tglErr).any { it != null }) {
            _uiState.update {
                it.copy(
                    formDeskripsiError = deskErr,
                    formSumberError    = sumberErr,
                    formMetodeError    = metodeErr,
                    formNominalError   = nominalErr,
                    formTanggalError   = tglErr
                )
            }
            return
        }

        val nominal = s.formNominal.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            try {
                if (s.editTarget != null) {
                    updatePemasukan(
                        s.editTarget.copy(
                            deskripsi = s.formDeskripsi.trim(),
                            sumber    = s.formSumber,
                            metode    = s.formMetode,
                            nominal   = nominal,
                            tanggal   = s.formTanggal,
                            catatan   = s.formCatatan.trim()
                        )
                    )
                } else {
                    addPemasukan(
                        Pemasukan(
                            deskripsi = s.formDeskripsi.trim(),
                            sumber    = s.formSumber,
                            metode    = s.formMetode,
                            nominal   = nominal,
                            tanggal   = s.formTanggal,
                            catatan   = s.formCatatan.trim()
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