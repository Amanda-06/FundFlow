// ============================================================
// feature/laporan/domain/usecase/LaporanExportUseCases.kt
// (FIXED — Export PDF/Excel + Auto-Open via FileProvider)
//
// !! PENTING !!
// File ini MENGGANTIKAN class `ExportPdfUseCase` dan
// `ExportExcelUseCase` yang ADA DI FILE `LaporanRepositoryUseCases.kt`.
//
// Langkah migrasi:
// 1. Buka LaporanRepositoryUseCases.kt
// 2. HAPUS seluruh class `ExportPdfUseCase` dan `ExportExcelUseCase`
//    beserta komentar section "// ── Export PDF ──" dan
//    "// ── Export Excel ──" (agar tidak duplicate class).
// 3. Letakkan file INI (LaporanExportUseCases.kt) di folder yang sama:
//    feature/laporan/domain/usecase/
// 4. Pastikan import yang sudah tidak terpakai di
//    LaporanRepositoryUseCases.kt (Context, PdfDocument, Paint,
//    HSSFWorkbook, FileOutputStream, Environment, File, ApplicationContext,
//    CurrencyFormatter) dihapus jika muncul warning "unused import"
//    — TIDAK WAJIB, hanya housekeeping.
// ============================================================
package com.example.fundflow.feature.laporan.domain.usecase

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.laporan.domain.model.LaporanDetailKeuangan
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Setelah file laporan (PDF/Excel) berhasil ditulis ke storage,
 * buka file tersebut melalui system chooser (ACTION_VIEW) menggunakan
 * FileProvider, agar user langsung melihat hasilnya (PDF viewer / Excel app)
 * — tanpa ini, user tidak punya cara untuk membuka file yang tersimpan
 * di app-private storage.
 *
 * PENTING — agar berfungsi, tambahkan ke AndroidManifest.xml di dalam
 * tag <application>:
 *
 * <provider
 *     android:name="androidx.core.content.FileProvider"
 *     android:authorities="${applicationId}.fileprovider"
 *     android:exported="false"
 *     android:grantUriPermissions="true">
 *     <meta-data
 *         android:name="android.support.FILE_PROVIDER_PATHS"
 *         android:resource="@xml/file_paths" />
 * </provider>
 *
 * dan buat res/xml/file_paths.xml (lihat file terpisah yang disediakan).
 */
internal fun openExportedFile(context: Context, file: File, mimeType: String) {
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
        // Tidak ada aplikasi yang bisa membuka tipe file ini di emulator/device.
        // File tetap tersimpan di app-private storage; abaikan agar tidak crash.
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

            // Buka file PDF langsung agar user mendapat feedback nyata
            openExportedFile(context, file, "application/pdf")

            file.absolutePath
        } catch (e: Exception) { null }
    }
}


// ── Export Excel ──────────────────────────────────────────────

class ExportExcelUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Hasilkan file Excel (.xls) dari data laporan detail keuangan,
     * lalu langsung BUKA via system chooser (Excel/Sheets viewer).
     * Return path file jika berhasil, null jika gagal.
     */
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

            (0..4).forEach { sheet.autoSizeColumn(it) }

            val dir  = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "laporan_fundflow_${System.currentTimeMillis()}.xls")
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()

            // Buka file Excel langsung agar user mendapat feedback nyata
            openExportedFile(context, file, "application/vnd.ms-excel")

            file.absolutePath
        } catch (e: Exception) { null }
    }
}