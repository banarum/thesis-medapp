package com.koenigmed.luomanager.presentation.mvp.profile.faq

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface FaqView : BaseView {

    fun showFaq(faq: String)
}