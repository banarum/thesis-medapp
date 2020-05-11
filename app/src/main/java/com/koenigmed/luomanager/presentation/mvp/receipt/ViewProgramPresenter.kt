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

    private var pulseForm1: PulseForm? = null
    private var pulseForm2: PulseForm? = null
    private var receiptPresentation = ReceiptPresentation()
    var program: MyoProgram? = null

    @SuppressLint("CheckResult")
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        /*receiptInteractor.getPulseForms()
                .subscribe({}, { Timber.e(it) })*/
    }

    @SuppressLint("CheckResult")
    fun setProgramPresentation(programId: String){
        programInteractor.getProgram(programId)
                .subscribe { program ->
                    this.program = program
                    this.setInfo(program)
                    this.setType(program.programType)
                    this.setTimes(program.startTimes!!)
                    Timber.d(program.myoProgramMyoTaskList.toString())
                }
    }

    private fun setType(programType: ProgramType) {
        val types = resourceManager.getStringArray(R.array.program_types)
        viewState.setProgramType(types[programType.number - 1])
    }

    private fun setInfo(program: MyoProgram) {
        viewState.setProgramTitle(program.name)
        viewState.setProgramDuration(TimeUnit.SECONDS.toMinutes(program.executionTimeS).toInt())
    }

    private fun setTimes(times: List<LocalTime>) {
        if (times.size == 1) {
            viewState.setProgramStartTime(times[0].getTimeString())
            viewState.setProgramEndTime(times[0].getTimeString())
        }else{
            val duration = Duration.between(times[0], times[1]).toMinutes()
            viewState.setProgramStartTime(times[0].minusMinutes(duration).getTimeString())
            viewState.setProgramEndTime(times.last().getTimeString())
        }
    }

    private fun setChannels(){

    }

    fun onBackPressed() = router.exit()

}