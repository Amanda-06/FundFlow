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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fundflow.R
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
            // FIX: reaktif terhadap tema
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header hijau dengan avatar ────────────────────
            // HeaderGreen tetap dipakai: ini warna brand yang sama di light/dark
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
                    // TextDark tetap: icon di atas header hijau brand
                    // FIX: Mengubah contentDescription "Kembali" menjadi stringResource
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint               = TextDark
                    )
                }

                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = stringResource(R.string.profile_title),
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark   // tetap: di atas header hijau
                    )
                    Spacer(Modifier.height(16.dp))

                    // Avatar — background putih tetap agar kontras di header hijau
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
                            tint     = HeaderGreen,   // tetap: warna brand
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = TextDark,   // tetap: di atas header hijau
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            // FIX: Mengubah hardcode nama fallback menjadi stringResource
                            text       = uiState.profile?.namaLengkap?.ifBlank { stringResource(R.string.profile_default_user) }
                                ?: stringResource(R.string.profile_default_user),
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color      = TextDark   // tetap: di atas header hijau
                        )
                        Text(
                            text  = stringResource(R.string.profile_role_bendahara),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark.copy(alpha = 0.7f)   // tetap: di atas header hijau
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
                    // FIX: reaktif terhadap tema
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    icon    = Icons.Default.AccountCircle,
                    label   = stringResource(R.string.profile_edit),
                    onClick = onNavigateToEditProfile
                )
                ProfileMenuItem(
                    icon    = Icons.Default.Info,
                    label   = stringResource(R.string.profile_about),
                    onClick = onNavigateToAbout
                )
                ProfileMenuItem(
                    icon    = Icons.AutoMirrored.Filled.HelpOutline,
                    label   = stringResource(R.string.profile_help_center),
                    onClick = onNavigateToHelp
                )
                // Keluar tetap ExpenseRed karena ini warna semantik/brand
                ProfileMenuItem(
                    icon      = Icons.AutoMirrored.Filled.Logout,
                    label     = stringResource(R.string.profile_logout),
                    iconTint  = ExpenseRed,
                    textColor = ExpenseRed,
                    onClick   = viewModel::onShowLogoutDialog
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                text      = stringResource(R.string.profile_version),
                style     = MaterialTheme.typography.bodySmall,
                // FIX: reaktif terhadap tema
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    // ── Dialog konfirmasi logout ──────────────────────────────
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissLogoutDialog,
            icon  = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ExpenseRed) },
            title = {
                Text(
                    stringResource(R.string.profile_logout_confirm_title),
                    style = MaterialTheme.typography.headlineSmall,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text  = {
                Text(
                    stringResource(R.string.profile_logout_confirm_message),
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmLogout(onLoggedOut) },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed,
                        contentColor   = Color.White
                    )
                ) {
                    if (uiState.isLoggingOut) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.profile_keluar), fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = viewModel::onDismissLogoutDialog) {
                    // FIX: reaktif terhadap tema
                    Text(stringResource(R.string.profile_batal), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            // FIX: reaktif terhadap tema
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

// ── Helper composables ────────────────────────────────────────
@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            // FIX: reaktif terhadap tema
            tint     = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            // FIX: reaktif terhadap tema
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    // FIX: default value pakai lambda agar ambil warna dari MaterialTheme saat render
    iconTint: Color  = Color.Unspecified,
    textColor: Color = Color.Unspecified
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        // FIX: reaktif terhadap tema
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                // FIX: gunakan onSurface jika tidak ada override warna eksplisit
                tint     = if (iconTint == Color.Unspecified)
                    MaterialTheme.colorScheme.onSurface
                else iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                label,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                // FIX: gunakan onSurface jika tidak ada override warna eksplisit
                color      = if (textColor == Color.Unspecified)
                    MaterialTheme.colorScheme.onSurface
                else textColor,
                modifier   = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                // FIX: reaktif terhadap tema
                tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}