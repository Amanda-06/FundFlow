package com.example.fundflow.feature.laporan.domain.usecase

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.laporan.domain.model.LaporanDetailKeuangan
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal fun openExportedFile(context: Context, file: File, mimeType: String) {

    // ── 1. Toast — SELALU muncul, ini bukti pasti file tersimpan ──
    Toast.makeText(
        context,
        "Laporan tersimpan: ${file.name}",
        Toast.LENGTH_LONG
    ).show()

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val viewIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val chooser = Intent.createChooser(viewIntent, "Buka Laporan FundFlow").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(chooser)
    } catch (e: Exception) {
    }
}


// ── Export PDF ────────────────────────────────────────────────

class ExportPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Hasilkan file PDF sederhana dari data laporan detail keuangan,
     * lalu langsung BUKA via system chooser (PDF viewer).
     * Return path file jika berhasil, null jika gagal.
     */
    operator fun invoke(laporan: LaporanDetailKeuangan): String? {
        return try {
            val pdfDoc   = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()   // A4
            val page     = pdfDoc.startPage(pageInfo)
            val canvas   = page.canvas

            val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true }
            val bodyPaint  = Paint().apply { textSize = 11f }
            val subPaint   = Paint().apply { textSize = 10f; color = android.graphics.Color.GRAY }

            var y = 60f
            val marginLeft = 40f

            // Judul
            canvas.drawText("LAPORAN DETAIL KEUANGAN", marginLeft, y, titlePaint); y += 30f
            canvas.drawText("Periode: ${laporan.periode}", marginLeft, y, subPaint);  y += 20f
            canvas.drawLine(marginLeft, y, 555f, y, subPaint);                        y += 16f

            // Pemasukan
            canvas.drawText("PEMASUKAN", marginLeft, y, bodyPaint.apply { isFakeBoldText = true }); y += 18f
            laporan.daftarPemasukan.forEach { item ->
                if (y > 800f) { pdfDoc.finishPage(page); return@forEach }
                canvas.drawText("${item.tanggal}  ${item.deskripsi}", marginLeft, y, bodyPaint.apply { isFakeBoldText = false }); y += 14f
                canvas.drawText(CurrencyFormatter.format(item.nominal), marginLeft + 20, y, subPaint); y += 16f
            }
            y += 8f
            canvas.drawText("Total Pemasukan: ${CurrencyFormatter.format(laporan.totalPemasukan)}", marginLeft, y, bodyPaint.apply { isFakeBoldText = true }); y += 24f

            // Pengeluaran
            canvas.drawText("PENGELUARAN", marginLeft, y, bodyPaint); y += 18f
            laporan.daftarPengeluaran.forEach { item ->
                if (y > 800f) { pdfDoc.finishPage(page); return@forEach }
                canvas.drawText("${item.tanggal}  ${item.deskripsi}", marginLeft, y, bodyPaint.apply { isFakeBoldText = false }); y += 14f
                canvas.drawText(CurrencyFormatter.format(item.nominal), marginLeft + 20, y, subPaint); y += 16f
            }
            y += 8f
            canvas.drawText("Total Pengeluaran: ${CurrencyFormatter.format(laporan.totalPengeluaran)}", marginLeft, y, bodyPaint); y += 24f
            canvas.drawLine(marginLeft, y, 555f, y, subPaint); y += 16f
            canvas.drawText("Saldo Akhir: ${CurrencyFormatter.format(laporan.saldoAkhir)}", marginLeft, y, titlePaint)

            pdfDoc.finishPage(page)

            val dir  = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "laporan_fundflow_${System.currentTimeMillis()}.pdf")
            pdfDoc.writeTo(FileOutputStream(file))
            pdfDoc.close()

            openExportedFile(context, file, "application/pdf")

            file.absolutePath
        } catch (e: Throwable) {
            null
        }
    }
}


// ── Export Excel ──────────────────────────────────────────────

class ExportExcelUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(laporan: LaporanDetailKeuangan): String? {
        return try {
            val workbook = HSSFWorkbook()
            val sheet    = workbook.createSheet("Laporan Keuangan")

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

            sheet.setColumnWidth(0, 14 * 256)
            sheet.setColumnWidth(1, 30 * 256)
            sheet.setColumnWidth(2, 18 * 256)
            sheet.setColumnWidth(3, 14 * 256)
            sheet.setColumnWidth(4, 16 * 256)

            val dir  = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "laporan_fundflow_${System.currentTimeMillis()}.xls")
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()

            openExportedFile(context, file, "application/vnd.ms-excel")

            file.absolutePath
        } catch (e: Throwable) {
            null
        }
    }
}