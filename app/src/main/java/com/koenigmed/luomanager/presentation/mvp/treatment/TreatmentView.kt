package com.koenigmed.luomanager.presentation.mvp.treatment

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.presentation.mvp.base.BaseView
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import org.threeten.bp.LocalDate

@StateStrategyType(AddToEndSingleStrategy::class)
interface TreatmentView : BaseView {

    fun setScreenState(state: TreatmentState)

    fun showTimeRunning(time: String)

    fun showProgram(myoProgram: MyoProgramPresentation)

    fun showHistory(dateTreatmentMap: Map<LocalDate, List<MyoProgramHistoryPresentation>>)

    fun setLoading(isLoading: Boolean, isSuccess: Boolean = false)

    fun setBattery(charge: Int)

    fun setBtPower(state: BtInteractor.BtPower)
}