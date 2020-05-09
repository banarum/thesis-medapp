package com.koenigmed.luomanager.presentation.mvp.settings

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : BaseView {

    fun initViews(deviceName: String, pushesEnabled: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showLogoutDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showUnbindDeviceDialog(deviceName: String)
}