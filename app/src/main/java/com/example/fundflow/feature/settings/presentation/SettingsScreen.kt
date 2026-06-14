package com.example.fundflow.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
import com.example.fundflow.ui.components.FundFlowTopBar
import com.example.fundflow.ui.theme.PrimaryLime
import com.example.fundflow.ui.theme.PrimaryLimeDark

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPeriode: () -> Unit,
    onLanguageChanged: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Restart activity saat bahasa berubah
    LaunchedEffect(uiState.needsRestart) {
        if (uiState.needsRestart) {
            viewModel.onRestartHandled()
            onLanguageChanged()
        }
    }

    Scaffold(
        topBar = {
            FundFlowTopBar(
                title          = stringResource(R.string.settings_title),
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Section: Umum ──────────────────────────────────
            SectionLabel(stringResource(R.string.settings_section_general))

            SettingsSwitchItem(
                icon            = Icons.Default.Notifications,
                title           = stringResource(R.string.settings_notifikasi),
                subtitle        = stringResource(R.string.settings_notifikasi_desc),
                checked         = uiState.isNotificationEnabled,
                onCheckedChange = viewModel::onToggleNotification
            )

            SettingsClickItem(
                icon    = Icons.Default.Language,
                title   = stringResource(R.string.settings_bahasa),
                value   = if (uiState.language == "id")
                    stringResource(R.string.settings_bahasa_id)
                else
                    stringResource(R.string.settings_bahasa_en),
                onClick = viewModel::onShowLanguageDialog
            )

            SettingsSwitchItem(
                icon     = if (uiState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                title    = stringResource(R.string.settings_tema),
                subtitle = if (uiState.isDarkTheme)
                    stringResource(R.string.settings_tema_dark)
                else
                    stringResource(R.string.settings_tema_light),
                checked         = uiState.isDarkTheme,
                onCheckedChange = viewModel::onToggleDarkTheme
            )

            Spacer(Modifier.height(12.dp))

            // ── Section: Organisasi ────────────────────────────
            SectionLabel(stringResource(R.string.settings_section_organisasi))

            SettingsClickItem(
                icon    = Icons.Default.CalendarMonth,
                title   = stringResource(R.string.settings_periode_kas),
                value   = stringResource(R.string.settings_periode_kas_desc),
                onClick = onNavigateToPeriode
            )
        }
    }

    // ── Dialog pilih bahasa ────────────────────────────────────
    if (uiState.showLanguageDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissLanguageDialog,
            title = {
                Text(
                    stringResource(R.string.settings_pilih_bahasa),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    LanguageOption(
                        label        = stringResource(R.string.settings_bahasa_id),
                        code         = "id",
                        selectedCode = uiState.language,
                        onSelect     = viewModel::onSelectLanguage
                    )
                    LanguageOption(
                        label        = stringResource(R.string.settings_bahasa_en),
                        code         = "en",
                        selectedCode = uiState.language,
                        onSelect     = viewModel::onSelectLanguage
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDismissLanguageDialog) {
                    Text(
                        stringResource(R.string.common_close),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ── Komponen reusable ──────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier   = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked         = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor   = MaterialTheme.colorScheme.surface,
                    checkedTrackColor   = PrimaryLime,
                    uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LanguageOption(
    label: String,
    code: String,
    selectedCode: String,
    onSelect: (String) -> Unit
) {
    val isSelected = code == selectedCode
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        onClick  = { onSelect(code) },
        shape    = MaterialTheme.shapes.small,
        color    = if (isSelected)
            PrimaryLime.copy(alpha = 0.2f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style      = MaterialTheme.typography.bodyMedium,
                color      = if (isSelected) PrimaryLimeDark
                else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier   = Modifier.weight(1f)
            )
            if (isSelected) Text("✓", color = PrimaryLimeDark, fontWeight = FontWeight.Bold)
        }
    }
}