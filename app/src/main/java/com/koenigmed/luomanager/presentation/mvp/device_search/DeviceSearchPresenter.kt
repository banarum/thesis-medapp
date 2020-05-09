package com.koenigmed.luomanager.presentation.mvp.device_search

import android.bluetooth.BluetoothDevice
import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class DeviceSearchPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val deviceInteractor: DeviceInteractor,
        private val btInteractor: BtInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<DeviceSearchView>() {

    private var screenState: DeviceSearchState = DeviceSearchState.NoBluetooth()

    init {
        btInteractor.setBtStateListener { enabled ->
            Timber.d("onBluetoothStateChanged $enabled")
            if (enabled) {
                if (screenState is DeviceSearchState.NoBluetooth) {
                    applySearch()
                }
            } else {
                if (screenState is DeviceSearchState.Running) {
                    applyNoBt()
                }
            }
        }
        btInteractor.setDeviceSearchListener(object : BtInteractor.DeviceSearchListener {
            override fun onError(message: String) {
                viewState.showMessage(message)
            }

            override fun onDevicePaired(deviceName: String) {
                Single.just("")
                        .observeOn(schedulers.ui())
                        .subscribe({
                            screenState = DeviceSearchState.Connected(deviceName)
                            deviceInteractor.setDeviceName(deviceName)
                            viewState.setScreenState(screenState)
                        }, {})
            }

            override fun onDevicesFound(devices: List<BluetoothDevice>) {
                screenState = DeviceSearchState.FewDevices(devices)
                viewState.setScreenState(screenState)
            }
        })
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.askForPermissionIfNeed()
    }

    fun onLocationPermissionGranted(granted: Boolean) {
        if (granted) viewState.showDataDialog() else router.exit()
    }

    private fun applyNoBt() {
        screenState = DeviceSearchState.NoBluetooth()
        viewState.setScreenState(screenState)
    }

    fun onDataDialogDismissed() {
        if (btInteractor.isBtEnabled()) {
            applySearch()
        } else {
            applyNoBt()
        }
    }

    private fun applySearch() {
        screenState = DeviceSearchState.Running()
        viewState.setScreenState(screenState)
        btInteractor.startSearch()
    }

    fun onSearchImageClick() {
        applySearch()
    }

    fun onNextButtonClick() {
        when (screenState) {
            is DeviceSearchState.NoBluetooth -> {
                applySearch()
            }
            is DeviceSearchState.Running -> {
                screenState = DeviceSearchState.Connected(deviceInteractor.getDeviceName())
                viewState.setScreenState(screenState)
            }
            is DeviceSearchState.Connected -> {
                router.replaceScreen(Screens.MAIN_SCREEN)
            }
        }
    }

    fun onHaveAnotherDeviceClick() {
        btInteractor.removeDevice()
        applySearch()
    }

    fun onDeviceChosen(device: BluetoothDevice) {
        btInteractor.onDeviceChosen(device)
    }

    override fun onDestroy() {
        super.onDestroy()
        btInteractor.stopSearch()
    }

    fun onBackPressed() = router.exit()
}