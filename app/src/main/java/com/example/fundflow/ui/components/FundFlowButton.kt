package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.Green500
import com.example.fundflow.ui.theme.White

@Composable
fun FundFlowPrimaryButton(
    text       : String,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
    enabled    : Boolean  = true,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = Green500,
            contentColor           = White,
            disabledContainerColor = Green500.copy(alpha = 0.4f),
            disabledContentColor   = White.copy(alpha = 0.6f)
        ),
        shape    = MaterialTheme.shapes.medium
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun FundFlowOutlinedButton(
    text       : String,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier,
    enabled    : Boolean  = true,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.height(52.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor = Green500
        ),
        border   = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.5.dp
        ),
        shape    = MaterialTheme.shapes.medium
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun FundFlowFab(
    onClick      : () -> Unit,
    icon         : ImageVector,
    contentDesc  : String = "Tambah",
    modifier     : Modifier = Modifier
) {
    FloatingActionButton(
        onClick           = onClick,
        modifier          = modifier,
        containerColor    = Green500,
        contentColor      = White,
        shape             = MaterialTheme.shapes.large
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = contentDesc
        )
    }
}
