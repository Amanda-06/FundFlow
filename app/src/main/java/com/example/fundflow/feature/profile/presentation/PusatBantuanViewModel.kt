package com.example.fundflow.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.example.fundflow.feature.profile.domain.model.FaqItem
import com.example.fundflow.feature.profile.domain.usecase.GetFaqListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PusatBantuanViewModel @Inject constructor(
    getFaqList: GetFaqListUseCase
) : ViewModel() {

    val faqList: List<FaqItem> = getFaqList()
}