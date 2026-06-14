package com.example.fundflow.feature.auth.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.ui.components.*
import com.example.fundflow.ui.theme.*

@Composable
fun RegisterStep1Screen(
    onNavigateToStep2: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Step Indicator ────────────────────────────────
            StepIndicator(currentStep = 1, totalSteps = 2)

            Spacer(Modifier.height(24.dp))

            // ── Header ────────────────────────────────────────
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text       = stringResource(R.string.register_title),
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = stringResource(R.string.register_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Form Fields ───────────────────────────────────
            FundFlowTextField(
                value         = uiState.namaLengkap,
                onValueChange = viewModel::onNamaLengkapChange,
                label         = stringResource(R.string.register_label_nama_lengkap),
                leadingIcon   = Icons.Default.Person,
                isError       = uiState.namaLengkapError != null,
                errorMessage  = uiState.namaLengkapError
            )
            Spacer(Modifier.height(14.dp))

            FundFlowTextField(
                value           = uiState.email,
                onValueChange   = viewModel::onEmailChange,
                label           = stringResource(R.string.register_label_email),
                leadingIcon     = Icons.Default.AlternateEmail,
                isError         = uiState.emailError != null,
                errorMessage    = uiState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(14.dp))

            FundFlowTextField(
                value         = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label         = stringResource(R.string.register_label_username),
                leadingIcon   = Icons.Default.AlternateEmail,
                isError       = uiState.usernameError != null,
                errorMessage  = uiState.usernameError
            )
            Spacer(Modifier.height(14.dp))

            FundFlowPasswordField(
                value         = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label         = stringResource(R.string.register_label_password),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.passwordError != null,
                errorMessage  = uiState.passwordError
            )
            Spacer(Modifier.height(14.dp))

            FundFlowPasswordField(
                value         = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label         = stringResource(R.string.register_label_confirm_password),
                leadingIcon   = Icons.Default.Lock,
                isError       = uiState.confirmPasswordError != null,
                errorMessage  = uiState.confirmPasswordError
            )

            Spacer(Modifier.height(28.dp))

            FundFlowPrimaryButton(
                text    = stringResource(R.string.register_btn_next),
                onClick = {
                    if (viewModel.validateStep1()) onNavigateToStep2()
                }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = stringResource(R.string.register_footer_has_account),
                    style = MaterialTheme.typography.bodyMedium,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FundFlowTextButton(
                    text = stringResource(R.string.register_footer_login_now),
                    onClick = onNavigateToLogin,
                    color = PrimaryLimeDark
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Komponen step indicator kecil ────────────────────────────
@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val stepNum  = index + 1
            val isActive = stepNum == currentStep
            val isDone   = stepNum < currentStep

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = when {
                            isActive || isDone -> PrimaryLime
                            else               -> MaterialTheme.colorScheme.outline
                        },
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "$stepNum",
                    style      = MaterialTheme.typography.labelMedium,
                    color      = if (isActive || isDone) TextDark
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            if (index < totalSteps - 1) {
                HorizontalDivider(
                    modifier  = Modifier.width(32.dp),
                    thickness = 2.dp,
                    color     = if (currentStep > stepNum) PrimaryLime
                    else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}