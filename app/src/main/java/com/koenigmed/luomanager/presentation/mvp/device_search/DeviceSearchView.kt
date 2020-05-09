package com.koenigmed.luomanager.presentation.mvp.device_search

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface DeviceSearchView : BaseView {
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDataDialog()
    fun setScreenState(state: DeviceSearchState)
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun askForPermissionIfNeed()
}