package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.fundflow.ui.theme.CardWhite
import com.example.fundflow.ui.theme.ExpenseRed
import com.example.fundflow.ui.theme.TextDark
import com.example.fundflow.ui.theme.TextLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundFlowTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.SemiBold,
                color      = TextDark,
                style      = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint               = TextDark
                    )
                }
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor        = CardWhite,
            titleContentColor     = TextDark,
            navigationIconContentColor = TextDark,
            actionIconContentColor = TextDark
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundFlowSelectionTopBar(
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text       = "$selectedCount / $totalCount dipilih",
                fontWeight = FontWeight.SemiBold,
                color      = CardWhite,
                style      = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector        = Icons.Default.Close,
                    contentDescription = "Batalkan seleksi",
                    tint               = CardWhite
                )
            }
        },
        actions = {
            // Select All
            TextButton(onClick = onSelectAll) {
                Text(
                    text  = "Semua",
                    color = CardWhite,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            // Delete
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Hapus terpilih",
                    tint               = ExpenseRed
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TextDark
        )
    )
}
