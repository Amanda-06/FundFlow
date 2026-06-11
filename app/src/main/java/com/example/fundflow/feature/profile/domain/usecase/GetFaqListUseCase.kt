package com.example.fundflow.feature.profile.domain.usecase

import com.example.fundflow.core.util.StringResProvider
import com.example.fundflow.R
import com.example.fundflow.feature.profile.domain.model.FaqItem
import javax.inject.Inject

/**
 * Mengembalikan daftar FAQ statis dari string resource
 * (otomatis mengikuti bahasa aktif — ID/EN).
 */
class GetFaqListUseCase @Inject constructor(
    private val stringRes: StringResProvider
) {
    operator fun invoke(): List<FaqItem> = listOf(
        FaqItem(stringRes.getString(R.string.help_faq_q1), stringRes.getString(R.string.help_faq_a1)),
        FaqItem(stringRes.getString(R.string.help_faq_q2), stringRes.getString(R.string.help_faq_a2)),
        FaqItem(stringRes.getString(R.string.help_faq_q3), stringRes.getString(R.string.help_faq_a3)),
        FaqItem(stringRes.getString(R.string.help_faq_q4), stringRes.getString(R.string.help_faq_a4)),
        FaqItem(stringRes.getString(R.string.help_faq_q5), stringRes.getString(R.string.help_faq_a5))
    )
}