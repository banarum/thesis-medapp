package com.koenigmed.luomanager.presentation.mvp.program

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.domain.interactor.program.ReceiptInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.presentation.mvp.receipt.DownloadStatus
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class DownloadProgramPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val receiptInteractor: ReceiptInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<DownloadProgramView>() {

    private var query: String? = null
    private var downlaodedProgram = false

    private lateinit var programsPresentation: DownloadProgramsPresentation

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        receiptInteractor.getProgramsToDownload(query)
                .subscribe(
                        {
                            Timber.d("showPrograms " + it)
                            programsPresentation = it
                            viewState.showPrograms(programsPresentation)
                        },
                        { Timber.e(it) }
                )
    }

    fun getPrograms(query: String?) = receiptInteractor.getProgramsToDownload(query)
            .doOnSubscribe { this.query = query }
            .toObservable()

    fun onBackPressed() = if (downlaodedProgram) {
        router.exitWithResult(Screens.RESULT_CODE_PROGRAM_ADDED, null)
    } else {
        router.exit()
    }

    fun downloadProgram(programId: String) {
        receiptInteractor.downloadProgram(programId)
                .subscribe(
                        {
                            downlaodedProgram = true
                            Timber.d("showPrograms " + it)
                            programsPresentation.changeProgramStatus(programId, DownloadStatus.COMPLETE)
                            viewState.showPrograms(programsPresentation)
                        },
                        {
                            Timber.e(it)
                            programsPresentation.changeProgramStatus(programId, DownloadStatus.NOT_DOWNLOADED)
                            viewState.showPrograms(programsPresentation)
                        }
                )
    }

}