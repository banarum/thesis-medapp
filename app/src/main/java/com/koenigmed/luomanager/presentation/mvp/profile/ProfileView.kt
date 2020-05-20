package com.koenigmed.luomanager.presentation.mvp.profile

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.presentation.mvp.base.BaseView

@StateStrategyType(AddToEndSingleStrategy::class)
interface ProfileView : BaseView {

    fun showFeelsThanks()

    fun showProfileData(profileData: UserProfilePresentation)

    fun setLoading(isLoading: Boolean, isSuccess: Boolean = false)

    fun setBattery(charge: Int)

    fun setBtPower(state: BtInteractor.BtPower)

    /*fun showMyoGraph(rightEntries: ArrayList<Entry>, leftEntries: ArrayList<Entry>)
    fun showEmptyMyoGraph()*/

}