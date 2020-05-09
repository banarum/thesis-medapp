package com.koenigmed.luomanager.presentation.mvp.program

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProgramView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showReceiptChooseDialog()

    fun showPrograms(programs: ProgramsPresentation)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgramsEdit()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showMultiSelectEnabled(multiSelect: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun selectProgram(userProgmar: Boolean, programId: String)
}