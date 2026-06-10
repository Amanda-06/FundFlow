package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FundFlowCard(
    modifier         : Modifier = Modifier,
    containerColor   : Color    = MaterialTheme.colorScheme.surface,
    contentPadding   : Dp       = 16.dp,
    elevation        : Dp       = 1.dp,
    onClick          : (() -> Unit)? = null,
    content          : @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick  = onClick,
            modifier = modifier,
            shape    = MaterialTheme.shapes.large,
            colors   = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content  = content
            )
        }
    } else {
        Card(
            modifier  = modifier,
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content  = content
            )
        }
    }
}

@Composable
fun FundFlowSummaryCard(
    title          : String,
    amount         : String,
    modifier       : Modifier = Modifier,
    containerColor : Color    = MaterialTheme.colorScheme.primary,
    contentColor   : Color    = MaterialTheme.colorScheme.onPrimary
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text  = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = amount,
                style = MaterialTheme.typography.headlineMedium,
                color = contentColor
            )
        }
    }
}
