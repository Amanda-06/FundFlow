// feature/onboarding/presentation/OnboardingScreen.kt
// ============================================================
package com.example.fundflow.feature.onboarding.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fundflow.ui.theme.*
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage(
        icon        = Icons.Default.AccountBalance,
        iconTint    = IuranBlue,   // tetap: warna semantik
        title       = "Kelola Keuangan Organisasi",
        description = "Catat iuran, pemasukan, dan pengeluaran\norganisasi kamu dengan mudah dan terstruktur."
    ),
    OnboardingPage(
        icon        = Icons.Default.GroupAdd,
        iconTint    = IncomeGreen,   // tetap: warna semantik
        title       = "Monitor Status Anggota",
        description = "Pantau siapa saja yang sudah dan belum\nmembayar iuran bulanan secara real-time."
    ),
    OnboardingPage(
        icon        = Icons.Default.Assessment,
        iconTint    = ReportOrange,   // tetap: warna semantik
        title       = "Laporan Otomatis",
        description = "Hasilkan laporan keuangan secara otomatis\ndan ekspor ke PDF atau Excel kapan saja."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            // FIX: reaktif terhadap tema
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        // ── Pager ─────────────────────────────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
        }

        // ── Dot Indicator ─────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.forEachIndexed { index, _ ->
                val isSelected = index == pagerState.currentPage
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label       = "dot_width"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        // PrimaryLime tetap: warna brand. Unselected FIX: reaktif
                        .background(if (isSelected) PrimaryLime else MaterialTheme.colorScheme.outline)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        // ── Tombol Aksi ───────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            if (isLastPage) {
                Button(
                    onClick = {
                        viewModel.markOnboardingDone()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLime,   // tetap: warna brand
                        contentColor   = TextDark       // tetap: di atas PrimaryLime
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text       = "Masuk",
                        fontWeight = FontWeight.SemiBold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            viewModel.markOnboardingDone()
                            onNavigateToLogin()
                        }
                    ) {
                        Text(
                            text  = "Lewati",
                            // FIX: reaktif terhadap tema
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryLime,   // tetap: warna brand
                            contentColor   = TextDark       // tetap
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text       = "Lanjut →",
                            fontWeight = FontWeight.SemiBold,
                            style      = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ── Konten satu slide ─────────────────────────────────────────
@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon dalam lingkaran — warna iconTint tetap: warna semantik per halaman
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(page.iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = page.icon,
                contentDescription = null,
                modifier           = Modifier.size(60.dp),
                tint               = page.iconTint   // tetap
            )
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text       = page.title,
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            // FIX: reaktif terhadap tema
            color      = MaterialTheme.colorScheme.onSurface,
            textAlign  = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text       = page.description,
            style      = MaterialTheme.typography.bodyLarge,
            // FIX: reaktif terhadap tema
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign  = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )
    }
}