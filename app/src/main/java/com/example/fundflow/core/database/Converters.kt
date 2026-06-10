package com.example.fundflow.core.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    // ── Date ↔ Long ──────────────────────────────────────────
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    // ── List<String> ↔ String (CSV) ──────────────────────────
    // Dipakai jika ada field yang menyimpan list sederhana (misal tag)
    @TypeConverter
    fun fromStringList(value: List<String>?): String? =
        value?.joinToString(separator = ",")

    @TypeConverter
    fun toStringList(value: String?): List<String>? =
        value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
}
