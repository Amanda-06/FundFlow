// feature/profile/presentation/PusatBantuanScreen.kt
// ============================================================
package com.example.fundflow.feature.profile.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fundflow.R
import com.example.fundflow.feature.profile.domain.model.FaqItem
import com.example.fundflow.ui.components.FundFlowTopBar
import com.example.fundflow.ui.theme.*

@Composable
fun PusatBantuanScreen(
    onNavigateBack: () -> Unit,
    viewModel: PusatBantuanViewModel = hiltViewModel()
) {
    val faqList = viewModel.faqList

    Scaffold(
        // FIX: Lokalisasi judul halaman TopBar
        topBar = { FundFlowTopBar(title = stringResource(R.string.help_title), onNavigateBack = onNavigateBack) },
        // FIX: reaktif terhadap tema
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    // FIX: Lokalisasi header bagian Pertanyaan Umum (FAQ)
                    text       = stringResource(R.string.help_faq_header),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface,
                    modifier   = Modifier.padding(bottom = 4.dp)
                )
            }

            items(faqList) { faq ->
                FaqCard(faq)
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                Text(
                    // FIX: Lokalisasi header bagian Hubungi Kami
                    text       = stringResource(R.string.help_contact_header),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface,
                    modifier   = Modifier.padding(bottom = 4.dp)
                )
            }

            item {
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
                        Icon(Icons.Default.Email, contentDescription = null, tint = IuranBlue)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                // FIX: Lokalisasi teks label Email Dukungan
                                text       = stringResource(R.string.help_support_email_label),
                                style      = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                // FIX: reaktif terhadap tema
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "support.fundflow@gmail.com", // Alamat email tetap literal karena merupakan nama entitas/tujuan alamat resmi
                                style = MaterialTheme.typography.bodySmall,
                                // FIX: reaktif terhadap tema
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        // FIX: reaktif terhadap tema
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    faq.question, // Nilai teks diambil secara dinamis dari object domain/API, sudah aman
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurface,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    // FIX: reaktif terhadap tema
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    faq.answer, // Nilai teks diambil secara dinamis dari object domain/API, sudah aman
                    style      = MaterialTheme.typography.bodySmall,
                    // FIX: reaktif terhadap tema
                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            }
        }
    }
}