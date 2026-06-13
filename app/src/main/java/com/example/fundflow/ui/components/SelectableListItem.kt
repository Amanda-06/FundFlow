package com.example.fundflow.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableListItem(
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = if (isSelected)
        PrimaryLime.copy(alpha = 0.15f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .combinedClickable(
                onClick     = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedVisibility(
            visible = isSelectionMode,
            enter   = expandHorizontally() + fadeIn(),
            exit    = shrinkHorizontally() + fadeOut()
        ) {
            Checkbox(
                checked         = isSelected,
                onCheckedChange = { onClick() },
                modifier        = Modifier.padding(end = 8.dp),
                colors          = CheckboxDefaults.colors(
                    checkedColor         = PrimaryLimeDark,
                    uncheckedColor       = TextLight,
                    checkmarkColor       = TextDark
                )
            )
        }

        content()
    }
}
