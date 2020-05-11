package com.koenigmed.luomanager.presentation.mvp.receipt

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import org.threeten.bp.LocalTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface ViewReceiptView : BaseView {
    fun setProgramTitle(title: String)
    fun setProgramType(typeName: String)
    fun setProgramDuration(duration: Int)
    fun setProgramStartTime(time: String)
    fun setProgramEndTime(time: String)
}