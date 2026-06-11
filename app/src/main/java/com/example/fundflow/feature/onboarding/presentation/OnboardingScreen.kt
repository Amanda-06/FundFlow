// ============================================================
// feature/onboarding/presentation/OnboardingScreen.kt  (FIXED)
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

// ── Data model untuk setiap slide onboarding ─────────────────
private data class OnboardingPage(
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage(
        icon        = Icons.Default.AccountBalance,
        iconTint    = IuranBlue,
        title       = "Kelola Keuangan Organisasi",
        description = "Catat iuran, pemasukan, dan pengeluaran\norganisasi kamu dengan mudah dan terstruktur."
    ),
    OnboardingPage(
        icon        = Icons.Default.GroupAdd,
        iconTint    = IncomeGreen,
        title       = "Monitor Status Anggota",
        description = "Pantau siapa saja yang sudah dan belum\nmembayar iuran bulanan secara real-time."
    ),
    OnboardingPage(
        icon        = Icons.Default.Assessment,
        iconTint    = ReportOrange,
        title       = "Laporan Otomatis",
        description = "Hasilkan laporan keuangan secara otomatis\ndan ekspor ke PDF atau Excel kapan saja."
    )
)

/**
 * Halaman Onboarding.
 *
 * Hanya ada SATU tombol aksi ("Masuk") yang mengarah ke LoginScreen.
 * Alur ke RegisterStep1 cukup melalui link "Daftar Sekarang" yang
 * sudah tersedia di LoginScreen — tidak perlu duplikasi tombol di sini.
 */
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
            .background(AppBackground)
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
            modifier            = Modifier.fillMaxWidth(),
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
                        .background(if (isSelected) PrimaryLime else BorderGray)
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
                // Halaman terakhir → satu tombol "Masuk" -> LoginScreen.
                // Link ke RegisterStep1 sudah ada di LoginScreen
                // ("Belum punya akun? Daftar Sekarang"), jadi tidak
                // perlu tombol terpisah di sini.
                Button(
                    onClick = {
                        viewModel.markOnboardingDone()
                        onNavigateToLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryLime,
                        contentColor   = TextDark
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
                // Halaman lain → tombol Lanjut + Lewati
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
                            color = TextLight,
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
                            containerColor = PrimaryLime,
                            contentColor   = TextDark
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
        // Ikon dalam lingkaran
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
                tint               = page.iconTint
            )
        }

        Spacer(Modifier.height(40.dp))

        // Judul
        Text(
            text       = page.title,
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color      = TextDark,
            textAlign  = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Deskripsi
        Text(
            text      = page.description,
            style     = MaterialTheme.typography.bodyLarge,
            color     = TextLight,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )
    }
}