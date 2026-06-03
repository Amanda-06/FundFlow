package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundFlowBottomSheet(
    onDismiss  : () -> Unit,
    sheetState : SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title      : String     = "",
    content    : @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        shape            = MaterialTheme.shapes.extraLarge,
        containerColor   = MaterialTheme.colorScheme.surface,
        dragHandle       = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text     = title,
                    style    = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
            }
            content()
        }
    }
}
