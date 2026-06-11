package com.example.fundflow.feature.iuran.data.model

import androidx.room.ColumnInfo

data class IuranSummaryRaw(
    @ColumnInfo(name = "totalTerkumpul")  val totalTerkumpul: Double,
    @ColumnInfo(name = "lunasCount")      val lunasCount: Int,
    @ColumnInfo(name = "belumBayarCount") val belumBayarCount: Int
)