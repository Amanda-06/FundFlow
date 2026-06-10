package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.*

@Composable
fun FundFlowPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick  = onClick,
        enabled  = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor        = PrimaryLime,
            contentColor          = TextDark,
            disabledContainerColor = DisabledGray,
            disabledContentColor   = TextLight
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color    = TextDark,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text       = text,
                fontWeight = FontWeight.SemiBold,
                style      = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun FundFlowSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextDark
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(

        ),
        shape  = MaterialTheme.shapes.medium
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.Medium,
            style      = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun FundFlowTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PrimaryLimeDark
) {
    TextButton(
        onClick  = onClick,
        modifier = modifier
    ) {
        Text(
            text       = text,
            color      = color,
            fontWeight = FontWeight.Medium,
            style      = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun FundFlowDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor  = ExpenseRed,
            contentColor    = Color.White
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text       = text,
            fontWeight = FontWeight.SemiBold,
            style      = MaterialTheme.typography.titleMedium
        )
    }
}
