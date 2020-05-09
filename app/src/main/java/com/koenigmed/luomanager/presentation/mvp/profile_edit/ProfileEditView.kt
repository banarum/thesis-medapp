package com.koenigmed.luomanager.presentation.mvp.profile_edit

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import org.threeten.bp.LocalDate

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProfileEditView : BaseView {

    fun showSurname(surname: String)
    fun showName(name: String)
    fun showAge(age: String)
    fun showHeight(height: String)
    fun showWeight(weight: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNameDialog(name: String)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSurnameDialog(surname: String)
    fun showProfileData(userData: UserDataPresentation)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showAgeDatePicker(date: LocalDate)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showHeightPicker(height: Int)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showWeightPicker(weight: Int)
}