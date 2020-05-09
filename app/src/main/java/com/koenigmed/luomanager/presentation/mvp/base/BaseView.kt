package com.koenigmed.luomanager.presentation.mvp.base

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Interface representing a View that will use to load data.
 */
interface BaseView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMessage(message: String)

    fun showProgress(isVisible: Boolean)
}
