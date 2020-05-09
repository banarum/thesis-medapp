package com.koenigmed.luomanager.presentation.mvp.receipt

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.program.ReceiptInteractor
import com.koenigmed.luomanager.domain.model.program.ProgramType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.extension.getTimeString
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class CreateProgramPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val receiptInteractor: ReceiptInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<CreateReceiptView>() {

    private var pulseForm1: PulseForm? = null
    private var pulseForm2: PulseForm? = null
    private var receiptPresentation = ReceiptPresentation()

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        receiptInteractor.getPulseForms()
                .subscribe({}, { Timber.e(it) })
    }

    fun onBackPressed() = router.exit()

    @SuppressLint("CheckResult")
    fun onSaveReceiptClick(
            name: String,
            executionTimeMin: Long,
            channel1Data: ChannelData,
            channel2Data: ChannelData
    ) {
        receiptPresentation.apply {
            this.name = name
            this.executionTimeS = TimeUnit.MINUTES.toSeconds(executionTimeMin)
            channel1Data.pulseForm = pulseForm1
            channel2Data.pulseForm = pulseForm2
            this.channel1Data = channel1Data
            this.channel2Data = channel2Data
        }
        if (!receiptPresentation.isValid()) {
            viewState.showMessage(resourceManager.getString(R.string.create_receipt_error_validate))
            return
        }
        receiptInteractor.saveReceipt(receiptPresentation)
                .subscribe(
                        { router.exitWithResult(Screens.RESULT_CODE_PROGRAM_ADDED, null) },
                        { Timber.e(it) }
                )
    }

    @SuppressLint("CheckResult")
    fun onPulseFormClick(channelIndex: Int) {
        val pulseForms = listOf(pulseForm1, pulseForm2)
        receiptInteractor.getPulseForms()
                .subscribe({ forms ->
                    val chosenFormIndex =
                            if (pulseForms[channelIndex] != null) {
                                forms.indexOf(pulseForms[channelIndex])
                            } else {
                                -1
                            }
                    viewState.showPulseFormsDialog(forms, chosenFormIndex, channelIndex)
                }, { Timber.e(it) })

    }

    fun onPulseFormChosen(pulseForm: PulseForm, channelIndex: Int) {
        when (channelIndex) {
            0 -> pulseForm1 = pulseForm
            1 -> pulseForm2 = pulseForm
        }
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

    fun onStartClick() {
        viewState.showTimePicker(true, receiptPresentation.startTime)
    }

    fun onEndClick() {
        viewState.showTimePicker(false, receiptPresentation.endTime)
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

}