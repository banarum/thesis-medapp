package com.koenigmed.luomanager.presentation.mvp.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import javax.inject.Inject

@InjectViewState
class MainActivityPresenter @Inject constructor(
        private val router: FlowRouter,
        private val btInteractor: BtInteractor,
        private val deviceInteractor: DeviceInteractor,
        private val authInteractor: AuthInteractor
) : MvpPresenter<MainActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (deviceInteractor.isDeviceConnected()) {
            router.replaceScreen(Screens.MAIN_SCREEN)
        } else {
            //router.replaceScreen(Screens.DEVICE_SEARCH_SCREEN)
            router.replaceScreen(Screens.MAIN_SCREEN)//todo!
        }
    }

    fun onBackPressed() = router.finishChain()
}