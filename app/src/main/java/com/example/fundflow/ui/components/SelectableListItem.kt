package com.example.fundflow.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.Green500

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableListItem(
    isSelectionMode : Boolean,
    isSelected      : Boolean,
    onClick         : () -> Unit,
    onLongClick     : () -> Unit,
    modifier        : Modifier = Modifier,
    content         : @Composable RowScope.() -> Unit
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick     = onClick,
                onLongClick = onLongClick
            ),
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment   = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = isSelectionMode,
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                Checkbox(
                    checked         = isSelected,
                    onCheckedChange = { onClick() },
                    colors          = CheckboxDefaults.colors(
                        checkedColor = Green500
                    ),
                    modifier        = Modifier.padding(end = 8.dp)
                )
            }
            content()
        }
    }
}
