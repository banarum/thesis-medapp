package com.koenigmed.luomanager.presentation.mvp.treatment

import android.annotation.SuppressLint
import android.os.Handler
import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.data.decice.Progress
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class TreatmentPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val programInteractor: ProgramInteractor,
        private val deviceInteractor: DeviceInteractor,
        private val btInteractor: BtInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<TreatmentView>() {

    private var screenState: TreatmentState? = null
    private var runningTimeDisposable: Disposable? = null

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (btInteractor.isDeviceConnected() && deviceInteractor.needSync()) {
            showSync()
        }

       btInteractor.chargeNotifier.observeOn(schedulers.ui()).subscribe({
            Timber.d("$it")
            if (it?.isCompleted() == true)
                viewState.setBattery((((it.result!!.voltage!!.dropLast(2).toFloat() - 2300f) / (4500f - 2300f))*100).toInt())
        }, {Timber.d("$it")})


        btInteractor.rssiNotifier
                .observeOn(schedulers.ui())
                .subscribe {
                    viewState.setBtPower(it)
                }

        onBtStateChange(btInteractor.btState)

        btInteractor.stateObservable.observeOn(schedulers.ui()).subscribe {
            onBtStateChange(it)
        }

        programInteractor.getSelectedProgram()
                .subscribe(
                        { program ->
                            viewState.showProgram(program)
                            screenState = when {
                                program.programType.isSchedule() -> TreatmentState.Schedule(program.startTimes!!)
                                else -> TreatmentState.Manual.Idle
                            }
                            viewState.setScreenState(screenState!!)
                        },
                        { Timber.e(it) }
                )
                .connect()
    }

    private fun showSync() {
        if (btInteractor.btState == BtInteractor.BT_CONNECTION_ACTIVE)
        Handler().post {
            router.navigateTo(Screens.SYNC_SCREEN)
        }


    }

    private fun onBtStateChange(state: Int) {
        when (state) {
            BtInteractor.BT_CONNECTION_PROGRESS ->
                viewState.setLoading(true)
            BtInteractor.BT_CONNECTION_ACTIVE ->
                viewState.setLoading(false, true)
            BtInteractor.BT_CONNECTION_INACTIVE ->
                viewState.setLoading(false, false)
        }
    }

    fun onBackPressed() = router.exit()

    fun onSyncClick() {
        if (!btInteractor.isDeviceConnected()) {
            Handler().post {
                router.navigateTo(Screens.DEVICE_SEARCH_SCREEN)
            }
            return
        }
        showSync()
    }

    fun onStartClick() {
        if (!btInteractor.isBtEnabled()) {
            screenState = TreatmentState.NoDeviceConnection()
            viewState.showMessage(resourceManager.getString(R.string.error_device_does_not_support_bluetooth))
            return
        }
        if (!btInteractor.isDeviceConnected()) {
            router.navigateTo(Screens.DEVICE_SEARCH_SCREEN)
            return
        }

        val callback = { progress: Progress ->
            if (progress.isComplete()) {
                toggleStartStop()
            } else if (progress.isError()) {
                viewState.showMessage(resourceManager.getString(progress.getErrorMessageId()))
            }
        }
        if (screenState is TreatmentState.Manual.Running) {
            btInteractor.sendStop(callback)
        } else if (screenState is TreatmentState.Manual.Idle) {
            btInteractor.sendStart(callback)
        }
    }

    private fun toggleStartStop() {
        (screenState as TreatmentState.Manual?)?.let { manual ->
            screenState = manual.toggle()
            Observable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { viewState.setScreenState(screenState as TreatmentState.Manual) }
            if (screenState == TreatmentState.Manual.Running()) {
                runningTimeDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                        .observeOn(schedulers.ui())
                        .subscribe(
                                {
                                    (screenState as TreatmentState.Manual.Running).plusSecond()
                                    viewState.showTimeRunning((screenState as TreatmentState.Manual.Running).getTimeRunningString())
                                },
                                { Timber.e(it) })
            } else {
                runningTimeDisposable?.dispose()
            }
        }
    }

    fun onProgramClick() {
        router.navigateTo(Screens.PROGRAM_SCREEN)
    }

    override fun onDestroy() {
        super.onDestroy()
        runningTimeDisposable?.dispose()
    }
}