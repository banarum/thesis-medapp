package com.koenigmed.luomanager.presentation.mvp.receipt

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import org.threeten.bp.LocalTime

@StateStrategyType(SkipStrategy::class)
interface ViewReceiptView : BaseView {
    fun setProgramTitle(title: String)
    fun setProgramType(typeName: String)
    fun setProgramDuration(duration: Int)
    fun setProgramStartTime(time: String)
    fun setProgramEndTime(time: String)
    fun showTimePicker(start: Boolean, time: LocalTime)
    fun showTimeSet(start: Boolean, time: String)
    fun showTypesDialog(types: Array<String>, positionSelected: Int)
    fun setIsSchedule(isSchedule: Boolean)
    fun setProgramChannels(data: List<ChannelData>)
}