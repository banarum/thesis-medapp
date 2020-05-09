package com.koenigmed.luomanager.presentation.mvp.settings

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import javax.inject.Inject

@InjectViewState
class SettingsPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val authInteractor: AuthInteractor,
        private val deviceInteractor: DeviceInteractor,
        private val prefsRepository: IPrefsRepository,
        private val btInteractor: BtInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<SettingsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initViews()
        btInteractor.setUnbindListener {
            initViews()
        }
    }

    private fun initViews() {
        val deviceName =
                if (deviceInteractor.isDeviceConnected()) {
                    deviceInteractor.getDeviceName()
                } else {
                    resourceManager.getString(R.string.settings_unbind_device_no_devices)
                }
        viewState.initViews(deviceName, prefsRepository.pushesEnabled)
    }

    fun onBackPressed() = router.exit()

    fun onDeviceIconClick() {
        if (deviceInteractor.isDeviceConnected()) {
            viewState.showUnbindDeviceDialog(deviceInteractor.getDeviceName())
        } else {
            router.navigateTo(Screens.DEVICE_SEARCH_SCREEN)
        }
    }

    fun unbindDevice() {
        btInteractor.unbindDevice()
    }

    fun onLogoutClick() {
        viewState.showLogoutDialog()
    }

    fun onPushesSwitchCheckChange(pushesEnabled: Boolean) {
        prefsRepository.pushesEnabled = pushesEnabled
    }

    fun logout() {
    }
}