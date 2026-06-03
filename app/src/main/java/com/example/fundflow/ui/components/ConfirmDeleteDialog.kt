package com.example.fundflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    count     : Int = 1,
    itemName  : String = "data",
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    val message = if (count == 1)
        "Yakin ingin menghapus $itemName ini? Tindakan ini tidak bisa dibatalkan."
    else
        "Yakin ingin menghapus $count $itemName? Tindakan ini tidak bisa dibatalkan."

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector        = Icons.Default.Delete,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text  = "Hapus $itemName",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor   = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = MaterialTheme.shapes.large
    )
}


