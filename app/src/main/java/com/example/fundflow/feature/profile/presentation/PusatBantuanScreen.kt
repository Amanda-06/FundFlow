// ============================================================
// feature/profile/presentation/PusatBantuanScreen.kt
// ============================================================
package com.example.fundflow.feature.profile.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
        topBar = { FundFlowTopBar(title = "Pusat Bantuan", onNavigateBack = onNavigateBack) },
        containerColor = AppBackground
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "Pertanyaan Umum (FAQ)",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextDark,
                    modifier   = Modifier.padding(bottom = 4.dp)
                )
            }

            items(faqList) { faq ->
                FaqCard(faq)
            }

            item { Spacer(Modifier.height(12.dp)) }

            item {
                Text(
                    "Hubungi Kami",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextDark,
                    modifier   = Modifier.padding(bottom = 4.dp)
                )
            }

            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = CardWhite),
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
                            Text("Email Dukungan", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextDark)
                            Text("support.fundflow@gmail.com", style = MaterialTheme.typography.bodySmall, color = TextLight)
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
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    faq.question,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color      = TextDark,
                    modifier   = Modifier.weight(1f)
                )
                Icon(
                    imageVector        = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint               = TextLight
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    faq.answer,
                    style      = MaterialTheme.typography.bodySmall,
                    color      = TextLight,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            }
        }
    }
}