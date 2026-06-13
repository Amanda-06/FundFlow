// ============================================================
// feature/anggota/presentation/AnggotaViewModel.kt
// ============================================================
package com.example.fundflow.feature.anggota.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.feature.anggota.data.repository.AnggotaRepositoryImpl // TAMBAHAN IMPORT
import com.example.fundflow.feature.anggota.domain.model.Anggota
import com.example.fundflow.feature.anggota.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnggotaViewModel @Inject constructor(
    private val getAnggotaList: GetAnggotaListUseCase,
    private val addAnggota: AddAnggotaUseCase,
    private val updateAnggota: UpdateAnggotaUseCase,
    private val deleteAnggota: DeleteAnggotaUseCase,
    private val deleteSelected: DeleteSelectedAnggotaUseCase,
    private val repository: AnggotaRepositoryImpl // TAMBAHAN REPOSITORI UNTUK SYNC
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnggotaState())
    val uiState: StateFlow<AnggotaState> = _uiState.asStateFlow()

    init {
        observeAnggota()
        fetchDataDariCloud() // TAMBAHAN CALL SINKRONISASI CLOUD
    }

    private fun observeAnggota() {
        getAnggotaList()
            .onEach { list ->
                _uiState.update { state ->
                    state.copy(
                        anggotaList  = list,
                        filteredList = filterList(list, state.searchQuery),
                        isLoading    = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
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

    // ── Search ────────────────────────────────────────────────
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery  = query,
                filteredList = filterList(state.anggotaList, query)
            )
        }
    }

    private fun filterList(list: List<Anggota>, query: String): List<Anggota> =
        if (query.isBlank()) list
        else list.filter { it.namaAnggota.contains(query, ignoreCase = true) }

    // ── Multi-select ──────────────────────────────────────────
    fun onItemLongClick(id: Int) {
        _uiState.update { it.copy(isSelectionMode = true, selectedIds = setOf(id)) }
    }

    fun onItemClick(id: Int) {
        val state = _uiState.value
        if (!state.isSelectionMode) return

        val newSelected = if (id in state.selectedIds)
            state.selectedIds - id else state.selectedIds + id

        _uiState.update {
            it.copy(
                selectedIds     = newSelected,
                isSelectionMode = newSelected.isNotEmpty()
            )
        }
    }

    fun onSelectAll() {
        val allIds = _uiState.value.filteredList.map { it.anggotaId }.toSet()
        _uiState.update { it.copy(selectedIds = allIds) }
    }

    fun onCancelSelection() {
        _uiState.update { it.copy(isSelectionMode = false, selectedIds = emptySet()) }
    }

    // ── Add / Edit Dialog ─────────────────────────────────────
    fun onShowAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, editTarget = null, inputNama = "", inputNamaError = null) }
    }

    fun onShowEditDialog(anggota: Anggota) {
        _uiState.update {
            it.copy(showAddDialog = true, editTarget = anggota, inputNama = anggota.namaAnggota, inputNamaError = null)
        }
    }

    fun onDismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false, editTarget = null, inputNama = "", inputNamaError = null) }
    }

    fun onInputNamaChange(v: String) {
        _uiState.update { it.copy(inputNama = v, inputNamaError = null) }
    }

    fun onSaveAnggota() {
        val state = _uiState.value
        val nama  = state.inputNama.trim()

        if (nama.isBlank()) {
            _uiState.update { it.copy(inputNamaError = "Nama tidak boleh kosong") }
            return
        }
        if (nama.length < 2) {
            _uiState.update { it.copy(inputNamaError = "Nama terlalu pendek") }
            return
        }

        viewModelScope.launch {
            try {
                if (state.editTarget != null) {
                    updateAnggota(state.editTarget.copy(namaAnggota = nama))
                } else {
                    addAnggota(nama)
                }
                _uiState.update { it.copy(showAddDialog = false, editTarget = null, inputNama = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    // ── Delete ────────────────────────────────────────────────
    fun onRequestDelete(id: Int) {
        _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = id) }
    }

    fun onRequestBatchDelete() {
        _uiState.update { it.copy(showDeleteDialog = true, deleteTargetId = null) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, deleteTargetId = null) }
    }

    fun onConfirmDelete() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                if (state.deleteTargetId != null) {
                    deleteAnggota(state.deleteTargetId)
                } else {
                    deleteSelected(state.selectedIds.toList())
                }
                _uiState.update {
                    it.copy(
                        showDeleteDialog  = false,
                        deleteTargetId    = null,
                        isSelectionMode   = false,
                        selectedIds       = emptySet()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, showDeleteDialog = false) }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}