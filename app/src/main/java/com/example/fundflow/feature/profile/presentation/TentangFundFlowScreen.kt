// feature/profile/presentation/TentangFundFlowScreen.kt
// ============================================================
package com.example.fundflow.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fundflow.ui.components.FundFlowTopBar
import com.example.fundflow.ui.theme.*

@Composable
fun TentangFundFlowScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = { FundFlowTopBar(title = "Tentang FundFlow", onNavigateBack = onNavigateBack) },
        // FIX: reaktif terhadap tema
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Logo ──────────────────────────────────────────
            // Background tetap HeaderGreen: warna brand
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(HeaderGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint     = TextDark,   // tetap: di atas header hijau brand
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "FundFlow",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                // FIX: reaktif terhadap tema
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Versi 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                // FIX: reaktif terhadap tema
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            // ── Deskripsi ─────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                // FIX: reaktif terhadap tema
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape     = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(
                    text      = "FundFlow adalah aplikasi mobile yang dirancang untuk membantu bendahara organisasi mengelola keuangan secara terstruktur, terpusat, dan efisien — mencakup pencatatan iuran, pemasukan, pengeluaran, hingga pelaporan otomatis.",
                    style     = MaterialTheme.typography.bodyMedium,
                    // FIX: reaktif terhadap tema
                    color     = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier  = Modifier.padding(16.dp),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Pengembang ────────────────────────────────────
            InfoCard(
                icon  = Icons.Default.Code,
                title = "Dikembangkan oleh",
                value = "Adinda Lestari & Amanda Arva Safaraya"
            )
            Spacer(Modifier.height(10.dp))
            InfoCard(
                icon  = Icons.Default.School,
                title = "Universitas",
                value = "Universitas Lambung Mangkurat"
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "© 2026 FundFlow. Seluruh hak cipta dilindungi.",
                style     = MaterialTheme.typography.bodySmall,
                // FIX: reaktif terhadap tema
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        // FIX: reaktif terhadap tema
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IuranBlue.copy(alpha = 0.12f)),   // tetap: warna semantik
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = IuranBlue,   // tetap: warna semantik
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    // FIX: reaktif terhadap tema
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TentangFundFlowScreenPreview() {
    FundFlowTheme {
        TentangFundFlowScreen(onNavigateBack = {})
    }
}