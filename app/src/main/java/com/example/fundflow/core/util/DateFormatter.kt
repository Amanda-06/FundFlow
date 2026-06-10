package com.example.fundflow.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val localeID = Locale("id", "ID")

    // Format standar yang dipakai di seluruh app
    private val displayFormat      = SimpleDateFormat("d MMM yyyy", localeID)         // 10 Apr 2026
    private val fullDisplayFormat  = SimpleDateFormat("EEEE, d MMMM yyyy", localeID)  // Jumat, 10 April 2026
    private val monthYearFormat    = SimpleDateFormat("MMMM yyyy", localeID)          // April 2026
    private val monthFormat        = SimpleDateFormat("MMMM", localeID)               // April
    private val storageFormat      = SimpleDateFormat("yyyy-MM-dd", localeID)         // 2026-04-10
    private val timeFormat         = SimpleDateFormat("HH:mm", localeID)              // 15:30

    /** Date → "10 Apr 2026" */
    fun formatDisplay(date: Date): String = displayFormat.format(date)

    /** Date → "Jumat, 10 April 2026" */
    fun formatFullDisplay(date: Date): String = fullDisplayFormat.format(date)

    /** Date → "April 2026" */
    fun formatMonthYear(date: Date): String = monthYearFormat.format(date)

    /** Date → "April" */
    fun formatMonth(date: Date): String = monthFormat.format(date)

    /** Date → "2026-04-10" (untuk disimpan ke Room / Firestore) */
    fun formatStorage(date: Date): String = storageFormat.format(date)

    /** Date → "15:30" */
    fun formatTime(date: Date): String = timeFormat.format(date)

    /** "yyyy-MM-dd" string → Date, atau null jika gagal parse */
    fun parseStorage(value: String): Date? = try {
        storageFormat.parse(value)
    } catch (e: Exception) { null }

    /** Timestamp Long → Date */
    fun fromTimestamp(timestamp: Long): Date = Date(timestamp)

    /** Kembalikan nama bulan berdasarkan nomor (1 = Januari, 12 = Desember) */
    fun getMonthName(monthNumber: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, monthNumber - 1)
        return monthFormat.format(cal.time)
    }

    /** Kembalikan tanggal hari ini sebagai Date */
    fun today(): Date = Date()

    /** Kembalikan pasangan Bulan dan Tahun saat ini sebagai Int */
    fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    fun currentYear(): Int  = Calendar.getInstance().get(Calendar.YEAR)
}
