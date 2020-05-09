package com.koenigmed.luomanager.presentation.mvp.program

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface DownloadProgramView : BaseView {

    fun showPrograms(programs: DownloadProgramsPresentation)

}