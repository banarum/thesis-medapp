package com.koenigmed.luomanager.presentation.mvp.sync

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.data.decice.Progress
import com.koenigmed.luomanager.data.mapper.program.ProgramMapper
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.domain.interactor.device.DeviceInteractor
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.util.RxUtil
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class SyncPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val errorHandler: ErrorHandler,
        private val btInteractor: BtInteractor,
        private val programInteractor: ProgramInteractor,
        private val programMapper: ProgramMapper,
        private val deviceInteractor: DeviceInteractor,
        private val schedulers: SchedulersProvider
) : BasePresenter<SyncView>() {

    var programId: String? = null
    private var programSet = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (!btInteractor.isBtEnabled()) {
            viewState.showMessage(resourceManager.getString(R.string.error_bluetooth_off))
            return
        }
        if (!btInteractor.isDeviceConnected()) {
            viewState.showSyncFailed(resourceManager.getString(R.string.error_device_connection))
            return
        }
        sync()
    }

    fun sync() {
        if (programId != null) sendProgram(programId!!) else syncTime()
    }

    @SuppressLint("CheckResult")
    fun sendProgram(programId: String) {
        programInteractor.getProgram(programId)
                .subscribe(
                        { program ->
                            btInteractor.sendProgram(program) { progress ->
                                handleProgress(progress)
                            }
                        },
                        {
                            Timber.e(it)
                        })
    }

    fun syncTime() {
        btInteractor.sync { progress ->
            handleProgress(progress)
        }
    }

    fun handleProgress(progress: Progress) {
        if (progress.isError()) {
            programSet = false
            viewState.showSyncFailed(resourceManager.getString(progress.getErrorMessageId()))
            return
        }
        viewState.showProgress(progress.progress)
        if (progress.progress == 100) {
            programSet = true
            deviceInteractor.setSynced()
            viewState.showSyncFinished()
            RxUtil.runWithDelay({
                exit()
            }, 1000)
                    .connect()
        }
    }

    fun onRetryClick() {
        viewState.showProgress(0)
        sync()
    }

    fun onBackPressed() = exit()

    fun exit() {
        if (programSet) {
            router.exitWithResult(Screens.RESULT_CODE_DEVICE_PROGRAM_SET, programId)
        } else {
            router.exitWithResult(Screens.RESULT_CODE_DEVICE_PROGRAM_SET, null)
        }
    }

}