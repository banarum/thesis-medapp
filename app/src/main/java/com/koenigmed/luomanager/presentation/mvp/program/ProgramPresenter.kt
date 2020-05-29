package com.koenigmed.luomanager.presentation.mvp.program

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.flow.Screens.RESULT_CODE_DEVICE_PROGRAM_SET
import com.koenigmed.luomanager.presentation.flow.Screens.RESULT_CODE_PROGRAM_ADDED
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.presentation.ui.receipt.ViewProgramBundle
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProgramPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val programInteractor: ProgramInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<ProgramView>() {

    private var query: String? = null
    private var multiSelect = false
    private var programIdsToDelete = mutableSetOf<String>()

    init {
        router.setResultListener(RESULT_CODE_DEVICE_PROGRAM_SET) { programId ->
            if (programId != null) {
                toggleSelectedProgram(programId as String)
            } else {
                getPrograms()
            }
        }
        router.setResultListener(RESULT_CODE_PROGRAM_ADDED) {_ ->
            getPrograms()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getPrograms()
    }

    fun getPrograms(multiSelect: Boolean = false) {
        programInteractor.getAllPrograms(query)
                .filter { it.filterDeleted(multiSelect, programIdsToDelete) }
                .subscribe(
                        { viewState.showPrograms(it) },
                        { Timber.e(it) }
                )
    }

    fun getPrograms(query: String?) = programInteractor.getAllPrograms(query)
            .filter { it.filterDeleted(multiSelect, programIdsToDelete) }
            .doOnSubscribe { this.query = query }
            .toObservable()

    fun onBackPressed() = router.exit()

    fun onProgramEditClick() {
        viewState.showProgramsEdit()
    }

    fun onSelectedProgramSwitch(programId: String) {
        router.navigateTo(Screens.SYNC_SCREEN, programId)
    }

    private fun toggleSelectedProgram(programId: String) {
        programInteractor.toggleSelectedProgram(programId)
                .doOnComplete { getPrograms() }
                .subscribe(
                        { },
                        { Timber.e(it) }
                )
    }

    fun onNewReceiptClick() {
        viewState.showReceiptChooseDialog()
    }

    fun onCreateReceiptChoose() {
        router.navigateTo(Screens.PROGRAM_CREATE_RECEIPT_SCREEN)
    }

    fun onDownloadReceiptChoose() {
        router.navigateTo(Screens.PROGRAM_DOWNLOAD_RECEIPT_SCREEN)
    }

    fun onMultiSelectEnabled(multiSelect: Boolean) {
        this.multiSelect = multiSelect
        viewState.showMultiSelectEnabled(multiSelect)
        if (!multiSelect) {
            programIdsToDelete.clear()
            getPrograms()
        }
    }

    fun onProgramAddedToDelete(programId: String) {
        programIdsToDelete.add(programId)
    }

    fun onDuplicateProgramClick(program: MyoProgramPresentation) {
        router.navigateTo(Screens.PROGRAM_CREATE_RECEIPT_SCREEN, program.id)
    }

    fun onEditProgramClick(program: MyoProgramPresentation) = onProgramSelected(program)

    fun onProgramDeleteClick(program: MyoProgramPresentation) {
        programInteractor.deletePrograms(mutableSetOf(program.id))
                .subscribe(
                        {
                            onMultiSelectEnabled(false)
                        },
                        { Timber.e(it) }
                )
    }

    fun onDeleteProgramsClick() {
        programInteractor.deletePrograms(programIdsToDelete)
                .subscribe(
                        {
                            onMultiSelectEnabled(false)
                        },
                        { Timber.e(it) }
                )
    }

    fun onProgramSelected(program: MyoProgramPresentation){
        router.navigateTo(Screens.PROGRAM_VIEW_RECEIPT_SCREEN, ViewProgramBundle(program.id, program.createdByUser))
    }

    override fun onDestroy() {
        super.onDestroy()
        router.removeResultListener(RESULT_CODE_DEVICE_PROGRAM_SET)
        router.removeResultListener(RESULT_CODE_PROGRAM_ADDED)
    }
}