// ui/components/FundFlowCard.kt

package com.example.fundflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.*

@Composable
fun FundFlowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.Unspecified,   // FIX: was CardWhite
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    // FIX: resolve di dalam Composable scope agar bisa baca MaterialTheme
    val resolvedColor = if (containerColor == Color.Unspecified)
        MaterialTheme.colorScheme.surface
    else
        containerColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors    = CardDefaults.cardColors(containerColor = resolvedColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape     = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content  = content
        )
    }
}

@Composable
fun FundFlowSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant  // FIX: was SurfaceGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape     = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content  = content
        )
    }
}

@Composable
fun FundFlowHeaderCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = HeaderGreen),  // tetap: warna brand
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape     = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content  = content
        )
    }
}