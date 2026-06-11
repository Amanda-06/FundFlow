package com.example.fundflow.feature.pengeluaran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.util.DateFormatter
import com.example.fundflow.feature.pengeluaran.domain.model.Pengeluaran
import com.example.fundflow.feature.pengeluaran.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengeluaranViewModel @Inject constructor(
    private val getPengeluaranList: GetPengeluaranListUseCase,
    private val addPengeluaran: AddPengeluaranUseCase,
    private val updatePengeluaran: UpdatePengeluaranUseCase,
    private val deletePengeluaran: DeletePengeluaranUseCase,
    private val deleteSelected: DeleteSelectedPengeluaranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PengeluaranState())
    val uiState: StateFlow<PengeluaranState> = _uiState.asStateFlow()

    init { observe() }

    private fun observe() {
        getPengeluaranList()
            .onEach { list ->
                _uiState.update { s ->
                    s.copy(
                        pengeluaranList  = list,
                        filteredList     = filter(list, s.searchQuery),
                        totalPengeluaran = list.sumOf { it.totalNominal },
                        isLoading        = false
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            .launchIn(viewModelScope)
    }

    fun onSearchChange(q: String) {
        _uiState.update { it.copy(searchQuery = q, filteredList = filter(it.pengeluaranList, q)) }
    }

    private fun filter(list: List<Pengeluaran>, q: String) =
        if (q.isBlank()) list
        else list.filter { it.deskripsi.contains(q, true) || it.kategori.contains(q, true) || it.namaProgram.contains(q, true) }

    // ── Selection ─────────────────────────────────────────────
    fun onItemLongClick(id: Int) = _uiState.update { it.copy(isSelectionMode = true, selectedIds = setOf(id)) }
    fun onItemClick(id: Int) {
        val s = _uiState.value; if (!s.isSelectionMode) return
        val n = if (id in s.selectedIds) s.selectedIds - id else s.selectedIds + id
        _uiState.update { it.copy(selectedIds = n, isSelectionMode = n.isNotEmpty()) }
    }
    fun onSelectAll() = _uiState.update { it.copy(selectedIds = it.filteredList.map { p -> p.pengeluaranId }.toSet()) }
    fun onCancelSelection() = _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) }

    // ── Form sheet ────────────────────────────────────────────
    fun onShowAddSheet() {
        _uiState.update {
            it.copy(
                showFormSheet = true, editTarget = null,
                formDeskripsi = "", formKategori = "", formNamaProgram = "",
                formMetode = "", formQuantity = "1", formHargaSatuan = "",
                formTanggal = DateFormatter.formatStorage(DateFormatter.today()), formCatatan = "",
                formDeskripsiError = null, formKategoriError = null,
                formMetodeError = null, formHargaSatuanError = null, formTanggalError = null
            )
        }
    }

    fun onShowEditSheet(p: Pengeluaran) {
        _uiState.update {
            it.copy(
                showFormSheet = true, editTarget = p,
                formDeskripsi = p.deskripsi, formKategori = p.kategori,
                formNamaProgram = p.namaProgram, formMetode = p.metode,
                formQuantity = p.quantity.toString(),
                formHargaSatuan = p.hargaSatuan.toLong().toString(),
                formTanggal = p.tanggal, formCatatan = p.catatan
            )
        }
    }

    fun onDismissSheet() = _uiState.update { it.copy(showFormSheet = false, editTarget = null) }

    fun onFormDeskripsiChange(v: String)   = _uiState.update { it.copy(formDeskripsi = v, formDeskripsiError = null) }
    fun onFormKategoriChange(v: String)    = _uiState.update { it.copy(formKategori = v, formKategoriError = null) }
    fun onFormNamaProgramChange(v: String) = _uiState.update { it.copy(formNamaProgram = v) }
    fun onFormMetodeChange(v: String)      = _uiState.update { it.copy(formMetode = v, formMetodeError = null) }
    fun onFormQuantityChange(v: String)    = _uiState.update { it.copy(formQuantity = v) }
    fun onFormHargaSatuanChange(v: String) = _uiState.update { it.copy(formHargaSatuan = v, formHargaSatuanError = null) }
    fun onFormTanggalChange(v: String)     = _uiState.update { it.copy(formTanggal = v, formTanggalError = null) }
    fun onFormCatatanChange(v: String)     = _uiState.update { it.copy(formCatatan = v) }

    fun onSave() {
        val s = _uiState.value
        val deskErr  = if (s.formDeskripsi.isBlank()) "Deskripsi tidak boleh kosong" else null
        val katErr   = if (s.formKategori.isBlank()) "Kategori harus dipilih" else null
        val metErr   = if (s.formMetode.isBlank()) "Metode harus dipilih" else null
        val hargaErr = if (s.formHargaSatuan.isBlank()) "Harga satuan tidak boleh kosong"
        else if ((s.formHargaSatuan.toDoubleOrNull() ?: 0.0) <= 0) "Harga satuan harus > 0" else null
        val tglErr   = if (s.formTanggal.isBlank()) "Tanggal tidak boleh kosong" else null

        if (listOf(deskErr, katErr, metErr, hargaErr, tglErr).any { it != null }) {
            _uiState.update { it.copy(formDeskripsiError = deskErr, formKategoriError = katErr, formMetodeError = metErr, formHargaSatuanError = hargaErr, formTanggalError = tglErr) }
            return
        }

        val qty    = s.formQuantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val harga  = s.formHargaSatuan.toDoubleOrNull() ?: 0.0
        val total  = qty * harga

        viewModelScope.launch {
            try {
                if (s.editTarget != null) {
                    updatePengeluaran(s.editTarget.copy(
                        deskripsi = s.formDeskripsi.trim(), kategori = s.formKategori,
                        namaProgram = s.formNamaProgram.trim(), metode = s.formMetode,
                        quantity = qty, hargaSatuan = harga, totalNominal = total,
                        tanggal = s.formTanggal, catatan = s.formCatatan.trim()
                    ))
                } else {
                    addPengeluaran(Pengeluaran(
                        deskripsi = s.formDeskripsi.trim(), kategori = s.formKategori,
                        namaProgram = s.formNamaProgram.trim(), metode = s.formMetode,
                        quantity = qty, hargaSatuan = harga, totalNominal = total,
                        tanggal = s.formTanggal, catatan = s.formCatatan.trim()
                    ))
                }
                _uiState.update { it.copy(showFormSheet = false, editTarget = null, successMessage = "Pengeluaran berhasil disimpan") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun onRequestDelete(id: Int)   = _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = id) }
    fun onRequestBatchDelete()     = _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = null) }
    fun onDismissDeleteDialog()    = _uiState.update { it.copy(showDeleteDialog = false, deleteTargetId = null) }
    fun onConfirmDelete() {
        val s = _uiState.value
        viewModelScope.launch {
            try {
                if (s.deleteTargetId != null) deletePengeluaran(s.deleteTargetId)
                else deleteSelected(s.selectedIds.toList())
                _uiState.update { it.copy(showDeleteDialog = false, deleteTargetId = null, isSelectionMode = false, selectedIds = emptySet()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(showDeleteDialog = false, errorMessage = e.message) }
            }
        }
    }
    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}