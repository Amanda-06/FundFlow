package com.example.fundflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundFlowTopBar(
    title            : String,
    onNavigateBack   : (() -> Unit)? = null,
    isSelectionMode  : Boolean = false,
    selectedCount    : Int = 0,
    onDeleteSelected : (() -> Unit)? = null,
    actionIcon       : ImageVector? = null,
    onActionClick    : (() -> Unit)? = null,
    scrollBehavior   : TopAppBarScrollBehavior? = null
) {
    val displayTitle = if (isSelectionMode) "$selectedCount dipilih" else title

    TopAppBar(
        title = {
            Text(
                text  = displayTitle,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }
        },
        actions = {
            if (isSelectionMode && onDeleteSelected != null) {
                IconButton(onClick = onDeleteSelected) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = "Hapus yang dipilih",
                        tint               = MaterialTheme.colorScheme.error
                    )
                }
            } else if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector        = actionIcon,
                        contentDescription = "Aksi"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor       = MaterialTheme.colorScheme.surface,
            titleContentColor    = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor     = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = scrollBehavior
    )
}
