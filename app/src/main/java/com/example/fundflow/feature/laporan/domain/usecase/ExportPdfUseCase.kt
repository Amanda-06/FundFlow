package com.example.fundflow.feature.laporan.domain.usecase

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.fundflow.core.util.CurrencyFormatter
import com.example.fundflow.feature.laporan.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ExportPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Hasilkan file PDF sederhana dari data laporan detail keuangan.
     * Return path file jika berhasil, null jika gagal.
     */
    operator fun invoke(laporan: LaporanDetailKeuangan): String? {
        return try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()   // A4
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

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
            file.absolutePath
        } catch (e: Exception) { null }
    }
}