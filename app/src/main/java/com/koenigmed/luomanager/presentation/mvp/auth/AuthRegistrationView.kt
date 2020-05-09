package com.koenigmed.luomanager.presentation.mvp.auth

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import org.threeten.bp.LocalDate

@StateStrategyType(AddToEndSingleStrategy::class)
interface AuthRegistrationView : BaseView {
    fun setRegisterButtonEnabled(enabled: Boolean)
    fun showGender(gender: String)
    fun showAge(age: String)
    fun showHeight(height: String)
    fun showWeight(weight: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAgeDatePicker(date: LocalDate)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showHeightPicker(position: Int)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showWeightPicker(position: Int)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGenderPicker(position: Int)
}