// ============================================================
// core/util/CurrencyFormatter.kt
// ============================================================
package com.example.fundflow.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val localeID = Locale("id", "ID")
    private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(localeID)

    /**
     * Format angka ke string Rupiah.
     * Contoh: 10000 → "Rp 10.000"
     */
    fun format(amount: Double): String {
        return "Rp ${numberFormat.format(amount)}"
    }

    /**
     * Format Long ke Rupiah — overload untuk kemudahan.
     */
    fun format(amount: Long): String = format(amount.toDouble())

    /**
     * Format Int ke Rupiah — overload.
     */
    fun format(amount: Int): String = format(amount.toDouble())

    /**
     * Parse string Rupiah kembali ke Double.
     * Input: "Rp 10.000" atau "10.000" → 10000.0
     * Jika gagal parse, return 0.0.
     */
    fun parse(value: String): Double {
        return try {
            val cleaned = value
                .replace("Rp", "")
                .replace("\\s".toRegex(), "")
                .replace(".", "")
                .replace(",", ".")
            cleaned.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    /**
     * Format singkat: angka besar disederhanakan.
     * Contoh: 1_500_000 → "Rp 1,5 Jt", 2_000_000_000 → "Rp 2 M"
     */
    fun formatShort(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> "Rp ${"%.1f".format(amount / 1_000_000_000)} M"
            amount >= 1_000_000     -> "Rp ${"%.1f".format(amount / 1_000_000)} Jt"
            amount >= 1_000         -> "Rp ${"%.1f".format(amount / 1_000)} Rb"
            else                    -> format(amount)
        }
    }
}
