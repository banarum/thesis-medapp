package com.koenigmed.luomanager.presentation.mvp.receipt

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.domain.interactor.program.ReceiptInteractor
import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.domain.model.program.ProgramType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.extension.getTimeString
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mapper.ProgramMapper
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ViewProgramPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val receiptInteractor: ReceiptInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider,
        private val programInteractor: ProgramInteractor
) : BasePresenter<ViewReceiptView>() {

    var receiptPresentation = ReceiptPresentation()
    private var programId: String? = null

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        /*receiptInteractor.getPulseForms()
                .subscribe({}, { Timber.e(it) })*/
    }

    @SuppressLint("CheckResult")
    fun setProgramPresentation(programId: String) {
        this.programId = programId
        programInteractor.getReceiptPresentation(programId)
                .subscribe { receiptPresentation ->
                    this.receiptPresentation = receiptPresentation
                    this.setInfo(receiptPresentation)
                    this.setType(receiptPresentation.programType)
                    setChannels(receiptPresentation)
                }
    }

    private fun setType(programType: ProgramType) {
        val types = resourceManager.getStringArray(R.array.program_types)
        viewState.setProgramType(types[programType.number - 1])

    }

    fun onStartClick() {
        viewState.showTimePicker(true, receiptPresentation.startTime)
    }

    fun onEndClick() {
        viewState.showTimePicker(false, receiptPresentation.endTime)
    }

    fun onStartProgram() {
        router.navigateTo(Screens.SYNC_SCREEN, this.programId)
    }

    fun onTimeChosen(isStart: Boolean, time: LocalTime) {
        if (isStart) {
            if (!time.isBefore(receiptPresentation.endTime)) {
                viewState.showMessage(resourceManager.getString(R.string.error_time))
                return
            }
            receiptPresentation.startTime = time
        } else {
            if (!time.isAfter(receiptPresentation.startTime)) {
                viewState.showMessage(resourceManager.getString(R.string.error_time))
                return
            }
            receiptPresentation.endTime = time
        }
        viewState.showTimeSet(isStart, time.getTimeString())
    }

    private fun setInfo(receiptPresentation: ReceiptPresentation) {
        viewState.setProgramTitle(receiptPresentation.name)
        viewState.setProgramDuration(TimeUnit.SECONDS.toMinutes(receiptPresentation.executionTimeS).toInt())
        viewState.setProgramStartTime(receiptPresentation.startTime.getTimeString())
        viewState.setProgramEndTime(receiptPresentation.endTime.getTimeString())
        viewState.setIsSchedule(receiptPresentation.programType.isSchedule())
    }

    @SuppressLint("CheckResult")
    private fun setChannels(program: ReceiptPresentation) {
        receiptInteractor.getPulseForms()
                .subscribe({ forms ->
                    program.channels.onEach {channel ->
                        channel.pulseForm = forms.find { it.id == channel.pulseForm?.id }
                    }
                    viewState.setProgramChannels(program.channels)
                }, { Timber.e(it) })

    }

    fun onProgramTypeClick() {
        val types = resourceManager.getStringArray(R.array.program_types)
        viewState.showTypesDialog(types, receiptPresentation.programType.number - 1)
    }

    fun onProgramTypeChosen(programTypePosition: Int) {
        val programType = ProgramType.fromNumber(programTypePosition + 1)
        viewState.setIsSchedule(programType.isSchedule())
        receiptPresentation.programType = programType
    }

    @SuppressLint("CheckResult")
    fun deleteProgram() {
        programInteractor.deletePrograms(mutableSetOf(this.programId!!)).subscribe({
            router.exitWithResult(Screens.RESULT_CODE_PROGRAM_ADDED, null)
        },
                { Timber.e(it) })
    }

    @SuppressLint("CheckResult")
    fun onSaveReceiptClick(executionTimeMin: Long) {

        receiptPresentation.apply {
            this.executionTimeS = TimeUnit.MINUTES.toSeconds(executionTimeMin)
        }
        if (!receiptPresentation.isValid()) {
            viewState.showMessage(resourceManager.getString(R.string.create_receipt_error_validate))
            return
        }
        receiptInteractor.saveReceipt(receiptPresentation)
                .subscribe(
                        {
                            programInteractor.deletePrograms(mutableSetOf(this.programId!!)).subscribe({
                                router.exitWithResult(Screens.RESULT_CODE_PROGRAM_ADDED, null)
                            },
                                    { Timber.e(it) })

                        },
                        { Timber.e(it) }
                )
    }

    fun onBackPressed() = router.exit()

}