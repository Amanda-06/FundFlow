package com.example.fundflow.feature.laporan.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundflow.core.firebase.FirebaseAuthService
import com.example.fundflow.feature.iuran.domain.usecase.GetPeriodeMonthsUseCase
import com.example.fundflow.feature.iuran.domain.usecase.MonthOption
import com.example.fundflow.feature.laporan.domain.model.LaporanDetailKeuangan
import com.example.fundflow.feature.laporan.domain.usecase.*

// TAMBAHKAN IMPORT SELURUH REPOSITORI IMPLEMENTASI UNTUK SINKRONISASI MASAL
import com.example.fundflow.feature.iuran.data.repository.PeriodeRepositoryImpl
import com.example.fundflow.feature.iuran.data.repository.IuranRepositoryImpl
import com.example.fundflow.feature.anggota.data.repository.AnggotaRepositoryImpl
import com.example.fundflow.feature.pemasukan.data.repository.PemasukanRepositoryImpl
import com.example.fundflow.feature.pengeluaran.data.repository.PengeluaranRepositoryImpl

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LaporanViewModel @Inject constructor(
    private val getPeriodeMonths: GetPeriodeMonthsUseCase,
    private val generateLaporanIuran: GenerateLaporanIuranUseCase,
    private val generateLaporanStatus: GenerateLaporanStatusUseCase,
    private val generateLaporanDetail: GenerateLaporanDetailUseCase,
    private val exportPdf: ExportPdfUseCase,
    private val exportExcel: ExportExcelUseCase,
    private val authService: FirebaseAuthService,

    // INJECT SELURUH REPOSITORI DATA MENTAH
    private val periodeRepository: PeriodeRepositoryImpl,
    private val anggotaRepository: AnggotaRepositoryImpl,
    private val iuranRepository: IuranRepositoryImpl,
    private val pemasukanRepository: PemasukanRepositoryImpl,
    private val pengeluaranRepository: PengeluaranRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(LaporanState())
    val uiState: StateFlow<LaporanState> = _uiState.asStateFlow()

    init {
        loadInitialDataAndSync()
    }

    private fun loadInitialDataAndSync() {
        viewModelScope.launch {
            // 1. Jalankan sinkronisasi background secara berurutan agar database lokal HP terisi penuh
            try {
                periodeRepository.syncWithCloud()
                anggotaRepository.syncWithCloud()
                iuranRepository.syncWithCloud()
                pemasukanRepository.syncWithCloud()
                pengeluaranRepository.syncWithCloud()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Setelah database lokal Room dipastikan terisi data cloud, baca range bulannya
            val userId = authService.currentUser?.uid.orEmpty()
            val months = getPeriodeMonths(userId)
            val currentKey = LocalDate.now().toString().substring(0, 7)
            val defaultMonth = months.find { it.key == currentKey } ?: months.firstOrNull()

            _uiState.update { it.copy(availableMonths = months, selectedMonthForStatus = defaultMonth) }
        }
    }

    // ── Laporan Iuran Bulanan ─────────────────────────────────
    fun onShowLaporanIuran() {
        val months = _uiState.value.availableMonths
        if (months.isEmpty()) return

        _uiState.update { it.copy(activeSheet = LaporanType.IURAN_BULANAN, isGenerating = true) }
        viewModelScope.launch {
            try {
                val start = months.first().key
                val end   = months.last().key
                val result = generateLaporanIuran(start, end)
                _uiState.update { it.copy(laporanIuran = result, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, errorMessage = e.message) }
            }
        }
    }

    // ── Laporan Status Bayar ──────────────────────────────────
    fun onShowLaporanStatus() {
        _uiState.update { it.copy(activeSheet = LaporanType.STATUS_BAYAR) }
        loadLaporanStatus()
    }

    fun onShowStatusMonthPicker()    = _uiState.update { it.copy(showStatusMonthPicker = true) }
    fun onDismissStatusMonthPicker() = _uiState.update { it.copy(showStatusMonthPicker = false) }

    fun onSelectStatusMonth(month: MonthOption) {
        _uiState.update { it.copy(selectedMonthForStatus = month, showStatusMonthPicker = false) }
        loadLaporanStatus()
    }

    private fun loadLaporanStatus() {
        val month = _uiState.value.selectedMonthForStatus ?: return
        _uiState.update { it.copy(isGenerating = true) }
        viewModelScope.launch {
            try {
                val result = generateLaporanStatus(month.bulan, month.tahun)
                _uiState.update { it.copy(laporanStatus = result, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, errorMessage = e.message) }
            }
        }
    }

    // ── Laporan Detail Keuangan ───────────────────────────────
    fun onShowLaporanDetail() {
        val months = _uiState.value.availableMonths
        if (months.isEmpty()) return

        _uiState.update { it.copy(activeSheet = LaporanType.DETAIL_KEUANGAN, isGenerating = true) }
        viewModelScope.launch {
            try {
                val start = "${months.first().key}-01"
                val end   = "${months.last().key}-28"   // batas aman akhir bulan
                val result = generateLaporanDetail(start, end)
                _uiState.update { it.copy(laporanDetail = result, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, errorMessage = e.message) }
            }
        }
    }

    // ── Dismiss sheet ─────────────────────────────────────────
    fun onDismissSheet() = _uiState.update {
        it.copy(activeSheet = null, laporanIuran = null, laporanStatus = null, laporanDetail = null)
    }

    // ── Export ────────────────────────────────────────────────
    fun onExportPdf() {
        val detail = buildExportableDetail() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val path = exportPdf(detail)
            _uiState.update {
                it.copy(
                    isExporting = false,
                    successMessage = if (path != null) "Laporan berhasil diekspor ke PDF" else null,
                    errorMessage    = if (path == null) "Gagal mengekspor laporan" else null,
                    exportedFilePath = path
                )
            }
        }
    }

    fun onExportExcel() {
        val detail = buildExportableDetail() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val path = exportExcel(detail)
            _uiState.update {
                it.copy(
                    isExporting = false,
                    successMessage = if (path != null) "Laporan berhasil diekspor ke Excel" else null,
                    errorMessage    = if (path == null) "Gagal mengekspor laporan" else null,
                    exportedFilePath = path
                )
            }
        }
    }

    /**
     * Bangun LaporanDetailKeuangan generik dari laporan aktif manapun,
     * agar Export PDF/Excel bisa dipakai di ketiga jenis laporan.
     */
    private fun buildExportableDetail(): LaporanDetailKeuangan? {
        val s = _uiState.value
        return when (s.activeSheet) {
            LaporanType.DETAIL_KEUANGAN -> s.laporanDetail

            LaporanType.IURAN_BULANAN -> s.laporanIuran?.let { iuran ->
                LaporanDetailKeuangan(
                    periode           = "Laporan Iuran Bulanan",
                    daftarPemasukan   = iuran.rincianBulan.map {
                        com.example.fundflow.feature.laporan.domain.model.ItemDetailKeuangan(
                            // FIX: Mengubah kombinasi angka tahun & bulan (Int) menjadi format String tanggal ISO standar ("yyyy-MM-dd")
                            tanggal    = LocalDate.of(it.tahun, it.bulan, 1).toString(),
                            deskripsi  = "Iuran Bulan ${it.bulan}",
                            keterangan = "Iuran Anggota",
                            nominal    = it.jumlah,
                            isIncome   = true
                        )
                    },
                    daftarPengeluaran = emptyList(),
                    totalPemasukan    = iuran.totalKeseluruhan,
                    totalPengeluaran  = 0.0,
                    saldoAkhir        = iuran.totalKeseluruhan
                )
            }

            LaporanType.STATUS_BAYAR -> s.laporanStatus?.let { status ->
                LaporanDetailKeuangan(
                    periode           = "Laporan Status Bayar - ${status.bulan}",
                    daftarPemasukan   = status.rincianAnggota.filter { it.statusBayar }.map {
                        com.example.fundflow.feature.laporan.domain.model.ItemDetailKeuangan(
                            tanggal = it.tanggalBayar ?: "-", deskripsi = it.namaAnggota, keterangan = "Lunas",
                            nominal = it.nominal, isIncome = true
                        )
                    },
                    daftarPengeluaran = emptyList(),
                    totalPemasukan    = status.totalTerkumpul,
                    totalPengeluaran  = 0.0,
                    saldoAkhir        = status.totalTerkumpul
                )
            }

            null -> null
        }
    }

    fun clearMessages() = _uiState.update { it.copy(errorMessage = null, successMessage = null, exportedFilePath = null) }
}