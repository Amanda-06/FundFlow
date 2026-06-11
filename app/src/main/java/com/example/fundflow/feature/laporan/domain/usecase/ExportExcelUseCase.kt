package com.example.fundflow.feature.laporan.domain.usecase

import android.content.Context
import android.os.Environment
import com.example.fundflow.feature.laporan.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ExportExcelUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(laporan: LaporanDetailKeuangan): String? {
        return try {
            val workbook  = HSSFWorkbook()
            val sheet     = workbook.createSheet("Laporan Keuangan")

            // Header row
            val headerRow = sheet.createRow(0)
            listOf("Tanggal", "Deskripsi", "Keterangan", "Jenis", "Nominal").forEachIndexed { i, title ->
                headerRow.createCell(i).setCellValue(title)
            }

            var rowIdx = 1
            // Pemasukan
            laporan.daftarPemasukan.forEach { item ->
                val row = sheet.createRow(rowIdx++)
                row.createCell(0).setCellValue(item.tanggal)
                row.createCell(1).setCellValue(item.deskripsi)
                row.createCell(2).setCellValue(item.keterangan)
                row.createCell(3).setCellValue("Pemasukan")
                row.createCell(4).setCellValue(item.nominal)
            }
            // Pengeluaran
            laporan.daftarPengeluaran.forEach { item ->
                val row = sheet.createRow(rowIdx++)
                row.createCell(0).setCellValue(item.tanggal)
                row.createCell(1).setCellValue(item.deskripsi)
                row.createCell(2).setCellValue(item.keterangan)
                row.createCell(3).setCellValue("Pengeluaran")
                row.createCell(4).setCellValue(item.nominal)
            }
            // Summary row
            val sumRow = sheet.createRow(rowIdx + 1)
            sumRow.createCell(3).setCellValue("Total Pemasukan")
            sumRow.createCell(4).setCellValue(laporan.totalPemasukan)
            val sumRow2 = sheet.createRow(rowIdx + 2)
            sumRow2.createCell(3).setCellValue("Total Pengeluaran")
            sumRow2.createCell(4).setCellValue(laporan.totalPengeluaran)
            val sumRow3 = sheet.createRow(rowIdx + 3)
            sumRow3.createCell(3).setCellValue("Saldo Akhir")
            sumRow3.createCell(4).setCellValue(laporan.saldoAkhir)

            (0..4).forEach { sheet.autoSizeColumn(it) }

            val dir  = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "laporan_fundflow_${System.currentTimeMillis()}.xls")
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            file.absolutePath
        } catch (e: Exception) { null }
    }
}