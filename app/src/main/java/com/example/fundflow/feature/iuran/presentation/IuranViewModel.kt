// ============================================================
// feature/iuran/presentation/IuranViewModel.kt
// ============================================================
package com.example.fundflow.feature.iuran.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.core.util.DateFormatter
import com.example.fundflow.feature.iuran.data.repository.IuranRepositoryImpl // TAMBAHAN IMPORT
import com.example.fundflow.feature.iuran.data.repository.PeriodeRepositoryImpl // TAMBAHAN IMPORT
import com.example.fundflow.feature.iuran.domain.model.Iuran
import com.example.fundflow.feature.iuran.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IuranViewModel @Inject constructor(
    private val getIuranList: GetIuranListUseCase,
    private val getIuranSummary: GetIuranSummaryUseCase,
    private val saveIuran: SaveIuranUseCase,
    private val getPeriodeMonths: GetPeriodeMonthsUseCase,
    private val authService: FirebaseAuthService,
    private val iuranRepository: IuranRepositoryImpl,  // TAMBAHAN UNTUK SYNC CLOUD IURAN
    private val periodeRepository: PeriodeRepositoryImpl // TAMBAHAN UNTUK SYNC CLOUD PERIODE KAS
) : ViewModel() {

    private val _uiState = MutableStateFlow(IuranState())
    val uiState: StateFlow<IuranState> = _uiState.asStateFlow()

    // Job untuk cancel observer lama saat bulan berganti
    private var observeJob: kotlinx.coroutines.Job? = null

    init {
        loadAvailableMonths()
    }

    private fun loadAvailableMonths() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // SEBELUM AMBIL BULAN, UNDUH DULU PERIODE KAS & IURAN DARI CLOUD FIRESTORE
            try {
                periodeRepository.syncWithCloud()
                iuranRepository.syncWithCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val userId = authService.currentUser?.uid.orEmpty()
            val months = getPeriodeMonths(userId)

            // Default: bulan berjalan jika ada dalam range, kalau tidak ambil bulan pertama
            val currentKey = DateFormatter.formatStorage(DateFormatter.today()).substring(0, 7) // "yyyy-MM"
            val defaultMonth = months.find { it.key == currentKey } ?: months.firstOrNull()

            _uiState.update { it.copy(availableMonths = months, selectedMonth = defaultMonth) }
            defaultMonth?.let { observeIuranData(it.bulan, it.tahun) }
        }
    }

    private fun observeIuranData(bulan: Int, tahun: Int) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                getIuranList(bulan, tahun),
                getIuranSummary(bulan, tahun)
            ) { list, summary -> list to summary }
                .onEach { (list, summary) ->
                    _uiState.update { state ->
                        state.copy(
                            iuranList    = list,
                            filteredList = applyFilters(list, state.searchQuery, state.activeFilter),
                            summary      = summary,
                            isLoading    = false
                        )
                    }
                }
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect()
        }
    }

    // ── Month Picker ──────────────────────────────────────────
    fun onShowMonthPicker()    = _uiState.update { it.copy(showMonthPicker = true) }
    fun onDismissMonthPicker() = _uiState.update { it.copy(showMonthPicker = false) }

    fun onSelectMonth(month: MonthOption) {
        _uiState.update { it.copy(selectedMonth = month, showMonthPicker = false, isLoading = true) }
        observeIuranData(month.bulan, month.tahun)
    }

    // ── Search & Filter ───────────────────────────────────────
    fun onSearchChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery  = query,
                filteredList = applyFilters(state.iuranList, query, state.activeFilter)
            )
        }
    }

    fun onFilterChange(filter: IuranFilter) {
        _uiState.update { state ->
            state.copy(
                activeFilter = filter,
                filteredList = applyFilters(state.iuranList, state.searchQuery, filter)
            )
        }
    }

    private fun applyFilters(list: List<Iuran>, query: String, filter: IuranFilter): List<Iuran> {
        var result = list
        if (query.isNotBlank()) {
            result = result.filter { it.namaAnggota.contains(query, ignoreCase = true) }
        }
        result = when (filter) {
            IuranFilter.SEMUA       -> result
            IuranFilter.LUNAS       -> result.filter { it.statusBayar }
            IuranFilter.BELUM_BAYAR -> result.filter { !it.statusBayar }
        }
        return result
    }

    // ── Bottom Sheet Detail Iuran ─────────────────────────────
    fun onItemClick(iuran: Iuran) {
        _uiState.update {
            it.copy(
                showDetailSheet  = true,
                selectedIuran    = iuran,
                formStatusBayar  = iuran.statusBayar,
                formTerlambat    = iuran.terlambat,
                formNominal      = if (iuran.nominal > 0) iuran.nominal.toLong().toString() else "",
                formMetode       = iuran.metodePembayaran,
                formTanggalBayar = iuran.tanggalBayar ?: DateFormatter.formatStorage(DateFormatter.today()),
                formCatatan      = iuran.catatan
            )
        }
    }

    fun onDismissDetailSheet() = _uiState.update { it.copy(showDetailSheet = false, selectedIuran = null) }

    // Form field setters
    fun onToggleStatusBayar(value: Boolean) {
        _uiState.update {
            it.copy(
                formStatusBayar = value,
                // Jika dimatikan, reset terlambat juga
                formTerlambat   = if (!value) false else it.formTerlambat
            )
        }
    }
    fun onToggleTerlambat(value: Boolean)     = _uiState.update { it.copy(formTerlambat = value) }
    fun onFormNominalChange(v: String)        = _uiState.update { it.copy(formNominal = v) }
    fun onFormMetodeChange(v: String)         = _uiState.update { it.copy(formMetode = v) }
    fun onFormTanggalBayarChange(v: String)   = _uiState.update { it.copy(formTanggalBayar = v) }
    fun onFormCatatanChange(v: String)        = _uiState.update { it.copy(formCatatan = v) }

    fun onSaveDetail() {
        val s = _uiState.value
        val target = s.selectedIuran ?: return

        val nominal = s.formNominal.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            try {
                saveIuran(
                    target.copy(
                        statusBayar      = s.formStatusBayar,
                        terlambat        = s.formTerlambat,
                        nominal          = nominal,
                        metodePembayaran = s.formMetode,
                        tanggalBayar     = if (s.formStatusBayar) s.formTanggalBayar else null,
                        catatan          = s.formCatatan.trim()
                    )
                )
                _uiState.update {
                    it.copy(showDetailSheet = false, selectedIuran = null, successMessage = "Data iuran berhasil disimpan")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null) }
}