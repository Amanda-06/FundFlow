package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun FundFlowTextField(
    value              : String,
    onValueChange      : (String) -> Unit,
    label              : String,
    modifier           : Modifier = Modifier,
    placeholder        : String   = "",
    leadingIcon        : ImageVector? = null,
    trailingIcon       : @Composable (() -> Unit)? = null,
    isError            : Boolean  = false,
    errorMessage       : String   = "",
    singleLine         : Boolean  = true,
    enabled            : Boolean  = true,
    readOnly           : Boolean  = false,
    keyboardOptions    : KeyboardOptions    = KeyboardOptions.Default,
    keyboardActions    : KeyboardActions    = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value               = value,
        onValueChange       = onValueChange,
        label               = { Text(label) },
        placeholder         = if (placeholder.isNotEmpty()) ({ Text(placeholder) }) else null,
        leadingIcon         = if (leadingIcon != null) ({
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                tint               = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }) else null,
        trailingIcon        = trailingIcon,
        isError             = isError,
        supportingText      = if (isError && errorMessage.isNotEmpty()) ({
            Text(
                text  = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }) else null,
        singleLine          = singleLine,
        enabled             = enabled,
        readOnly            = readOnly,
        keyboardOptions     = keyboardOptions,
        keyboardActions     = keyboardActions,
        visualTransformation = visualTransformation,
        shape               = MaterialTheme.shapes.medium,
        colors              = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor     = MaterialTheme.colorScheme.error
        ),
        modifier            = modifier.fillMaxWidth()
    )
}
