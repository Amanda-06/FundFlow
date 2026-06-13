//FundFlowTextField.kt

package com.example.fundflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.theme.*

@Composable
fun FundFlowTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value          = value,
            onValueChange  = onValueChange,
            label          = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
            placeholder    = if (placeholder.isNotEmpty()) {
                { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = TextMuted) }
            } else null,
            leadingIcon    = if (leadingIcon != null) {
                { Icon(imageVector = leadingIcon, contentDescription = null, tint = TextLight) }
            } else null,
            trailingIcon   = trailingIcon,
            visualTransformation = visualTransformation,
            isError        = isError,
            enabled        = enabled,
            singleLine     = singleLine,
            maxLines       = maxLines,
            keyboardOptions  = keyboardOptions,
            keyboardActions  = keyboardActions,
            modifier       = Modifier.fillMaxWidth(),
            shape          = MaterialTheme.shapes.small,
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryLimeDark,
                unfocusedBorderColor = BorderGray,
                errorBorderColor     = ExpenseRed,
                focusedLabelColor    = PrimaryLimeDark,
                unfocusedLabelColor  = TextLight,
                cursorColor          = PrimaryLimeDark,
                focusedTextColor     = TextDark,
                unfocusedTextColor   = TextDark,
                errorTextColor       = TextDark,
                disabledTextColor    = TextMuted,
                disabledBorderColor  = BorderGray
            )
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = errorMessage,
                color = ExpenseRed,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun FundFlowPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            label         = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
            leadingIcon   = if (leadingIcon != null) {
                { Icon(imageVector = leadingIcon, contentDescription = null, tint = TextLight) }
            } else null,
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Sembunyikan password"
                        else "Tampilkan password",
                        tint = TextLight
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            isError       = isError,
            singleLine    = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier      = Modifier.fillMaxWidth(),
            shape         = MaterialTheme.shapes.small,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PrimaryLimeDark,
                unfocusedBorderColor = BorderGray,
                errorBorderColor     = ExpenseRed,
                focusedLabelColor    = PrimaryLimeDark,
                unfocusedLabelColor  = TextLight,
                cursorColor          = PrimaryLimeDark,
                focusedTextColor     = TextDark,
                unfocusedTextColor   = TextDark,
                errorTextColor       = TextDark
            )
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text  = errorMessage,
                color = ExpenseRed,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}