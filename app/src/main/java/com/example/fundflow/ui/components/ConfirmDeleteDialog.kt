package com.example.fundflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.fundflow.ui.theme.*


@Composable
fun ConfirmDeleteDialog(
    title: String = "Hapus Data?",
    message: String = "Tindakan ini tidak dapat dibatalkan.",
    confirmLabel: String = "Hapus",
    dismissLabel: String = "Batal",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector        = Icons.Default.DeleteForever,
                contentDescription = null,
                tint               = ExpenseRed
            )
        },
        title = {
            Text(
                text  = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextDark
            )
        },
        text = {
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextLight
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = ExpenseRed,
                    contentColor   = Color.White
                )
            ) {
                Text(text = confirmLabel, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = dismissLabel, style = MaterialTheme.typography.labelLarge, color = TextDark)
            }
        },
        containerColor = CardWhite,
        iconContentColor = ExpenseRed,
        titleContentColor = TextDark
    )
}

@Composable
fun ConfirmBatchDeleteDialog(
    itemCount: Int,
    itemName: String = "data",
    extraWarning: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val message = buildString {
        append("$itemCount $itemName yang dipilih akan dihapus secara permanen.")
        if (extraWarning != null) {
            append("\n\n$extraWarning")
        }
    }
    ConfirmDeleteDialog(
        title   = "Hapus $itemCount ${itemName.replaceFirstChar { it.uppercase() }}?",
        message = message,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
