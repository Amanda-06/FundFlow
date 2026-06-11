// ============================================================
// feature/profile/presentation/ProfileScreen.kt
// ============================================================
package com.example.fundflow.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header hijau dengan avatar ────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderGreen)
                    .statusBarsPadding()
            ) {
                // Tombol kembali
                IconButton(
                    onClick  = onNavigateBack,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextDark)
                }

                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = "Profil",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark
                    )
                    Spacer(Modifier.height(16.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CardWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint               = HeaderGreen,
                            modifier           = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextDark, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text       = uiState.profile?.namaLengkap?.ifBlank { "Pengguna FundFlow" } ?: "Pengguna FundFlow",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color      = TextDark
                        )
                        Text(
                            text  = "Bendahara",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // ── Info kontak ────────────────────────────────────
            if (!uiState.isLoading && uiState.profile != null) {
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    colors    = CardDefaults.cardColors(containerColor = CardWhite),
                    shape     = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow(icon = Icons.Default.Email, text = uiState.profile!!.email)
                        if (uiState.profile!!.namaOrganisasi.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            InfoRow(icon = Icons.Default.Business, text = uiState.profile!!.namaOrganisasi)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Menu list ──────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileMenuItem(
                    icon  = Icons.Default.AccountCircle,
                    label = "Edit Profil",
                    onClick = onNavigateToEditProfile
                )
                ProfileMenuItem(
                    icon  = Icons.Default.Info,
                    label = "Tentang FundFlow",
                    onClick = onNavigateToAbout
                )
                ProfileMenuItem(
                    icon  = Icons.AutoMirrored.Filled.HelpOutline,
                    label = "Pusat Bantuan",
                    onClick = onNavigateToHelp
                )
                ProfileMenuItem(
                    icon       = Icons.AutoMirrored.Filled.Logout,
                    label      = "Keluar",
                    iconTint   = ExpenseRed,
                    textColor  = ExpenseRed,
                    onClick    = viewModel::onShowLogoutDialog
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text       = "Versi 1.0.0",
                style      = MaterialTheme.typography.bodySmall,
                color      = TextMuted,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    // ── Dialog konfirmasi logout ──────────────────────────────
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissLogoutDialog,
            icon  = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ExpenseRed) },
            title = { Text("Keluar dari Akun?", style = MaterialTheme.typography.headlineSmall, color = TextDark) },
            text  = { Text("Anda perlu masuk kembali untuk mengakses data keuangan organisasi.", color = TextLight) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmLogout(onLoggedOut) },
                    colors  = ButtonDefaults.buttonColors(containerColor = ExpenseRed, contentColor = Color.White)
                ) {
                    if (uiState.isLoggingOut) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Keluar", fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = viewModel::onDismissLogoutDialog) {
                    Text("Batal", color = TextDark)
                }
            },
            containerColor = CardWhite
        )
    }
}

// ── Helper composables ────────────────────────────────────────
@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextLight, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextDark)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color  = TextDark,
    textColor: Color = TextDark
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text(
                label,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = textColor,
                modifier   = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}