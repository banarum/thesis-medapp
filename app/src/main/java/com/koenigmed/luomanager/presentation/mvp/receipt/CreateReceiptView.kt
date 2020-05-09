package com.koenigmed.luomanager.presentation.mvp.receipt

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import org.threeten.bp.LocalTime

@StateStrategyType(AddToEndSingleStrategy::class)
interface CreateReceiptView : BaseView {
    fun showPulseFormsDialog(forms: List<PulseForm>, checkedItemPosition: Int, channelIndex: Int)
    fun showTypesDialog(types: Array<String>, positionSelected: Int)
    fun showTimePicker(start: Boolean, time: LocalTime)
    fun showTimeSet(start: Boolean, time: String)
    fun setIsSchedule(isSchedule: Boolean)
}