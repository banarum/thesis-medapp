package com.koenigmed.luomanager.presentation.mvp.sync

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface SyncView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress(percent: Int)

    fun showSyncFinished()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSyncFailed(message: String)
}