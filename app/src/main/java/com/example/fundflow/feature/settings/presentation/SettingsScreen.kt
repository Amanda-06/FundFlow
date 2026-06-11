// ============================================================
// feature/settings/presentation/SettingsScreen.kt
// ============================================================
package com.example.fundflow.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Ini otomatis mencakup Icons.Default / Icons.Filled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.components.FundFlowTopBar
import com.example.fundflow.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPeriode: () -> Unit,
    onLanguageChanged: () -> Unit,   // trigger Activity recreate dari MainActivity
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
        topBar = { FundFlowTopBar(title = "Pengaturan", onNavigateBack = onNavigateBack) },
        containerColor = AppBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Section: Umum ──────────────────────────────────
            SectionLabel("UMUM")

            SettingsSwitchItem(
                icon        = Icons.Default.Notifications,
                title       = "Notifikasi",
                subtitle    = "Pengingat pembayaran iuran dan aktivitas keuangan",
                checked     = uiState.isNotificationEnabled,
                onCheckedChange = viewModel::onToggleNotification
            )

            SettingsClickItem(
                icon     = Icons.Default.Language,
                title    = "Bahasa",
                value    = if (uiState.language == "id") "Bahasa Indonesia" else "English",
                onClick  = viewModel::onShowLanguageDialog
            )

            SettingsSwitchItem(
                icon        = if (uiState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                title       = "Tema",
                subtitle    = if (uiState.isDarkTheme) "Mode Gelap" else "Mode Terang",
                checked     = uiState.isDarkTheme,
                onCheckedChange = viewModel::onToggleDarkTheme
            )

            Spacer(Modifier.height(12.dp))

            // ── Section: Organisasi ────────────────────────────
            SectionLabel("ORGANISASI")

            SettingsClickItem(
                icon     = Icons.Default.CalendarMonth,
                title    = "Pengaturan Periode Kas",
                value    = "Atur periode kepengurusan",
                onClick  = onNavigateToPeriode
            )
        }
    }

    // ── Dialog pilih bahasa ────────────────────────────────────
    if (uiState.showLanguageDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissLanguageDialog,
            title = { Text("Pilih Bahasa", style = MaterialTheme.typography.titleLarge, color = TextDark) },
            text = {
                Column {
                    LanguageOption("Bahasa Indonesia", "id", uiState.language, viewModel::onSelectLanguage)
                    LanguageOption("English", "en", uiState.language, viewModel::onSelectLanguage)
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onDismissLanguageDialog) {
                    Text("Tutup", color = TextDark)
                }
            },
            containerColor = CardWhite
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
        color      = TextLight,
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
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextDark)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextLight)
            }
            Switch(
                checked         = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor   = CardWhite,
                    checkedTrackColor   = PrimaryLime,
                    uncheckedThumbColor = CardWhite,
                    uncheckedTrackColor = BorderGray
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
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextDark)
                Text(value, style = MaterialTheme.typography.bodySmall, color = TextLight)
            }
            // PERBAIKAN: Mengubah dari Icons.AutoMirrored.Filled.ChevronRight ke Icons.Default.ChevronRight
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
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
        color    = if (isSelected) PrimaryLime.copy(alpha = 0.2f) else CardWhite
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style      = MaterialTheme.typography.bodyMedium,
                color      = if (isSelected) PrimaryLimeDark else TextDark,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier   = Modifier.weight(1f)
            )
            if (isSelected) Text("✓", color = PrimaryLimeDark, fontWeight = FontWeight.Bold)
        }
    }
}