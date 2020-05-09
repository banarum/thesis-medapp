package com.koenigmed.luomanager.domain.interactor.program

import com.koenigmed.luomanager.data.repository.program.IProgramRepository
import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.presentation.mapper.ProgramMapper
import com.koenigmed.luomanager.presentation.mvp.program.DownloadProgramsPresentation
import com.koenigmed.luomanager.presentation.mvp.receipt.ReceiptPresentation
import com.koenigmed.luomanager.system.SchedulersProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class ReceiptInteractor
@Inject constructor(private val programRepository: IProgramRepository,
                    private val programMapper: ProgramMapper,
                    private val schedulers: SchedulersProvider) {

    fun saveReceipt(receipt: ReceiptPresentation): Completable {
        return programRepository.saveReceipt(programMapper.mapToProgram(receipt))
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }

    fun getPulseForms(): Single<List<PulseForm>> {
        return programRepository.getPulseForms()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }

    fun getProgramsToDownload(query: String? = null): Single<DownloadProgramsPresentation> {
        return Single.zip(programRepository.getProgramsFromNetwork(), programRepository.getPrograms(),
                BiFunction<List<MyoProgram>, List<MyoProgram>, DownloadProgramsPresentation> { programsFromNetwork, programsFromDb ->
                    val programs = programsFromNetwork
                            .filter {
                                filter(query, it)
                            }
                            .filter {
                                !programsFromDb.contains(it)
                            }

                    DownloadProgramsPresentation(programs.map { programMapper.mapToDownloadPresentation(it) })
                })
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }

    private fun filter(query: String?, it: MyoProgram): Boolean {
        if (query != null) {
            return it.name.toLowerCase().contains(query.trim().toLowerCase())
        }
        return true
    }

    fun downloadProgram(programId: String): Single<Any> {
        return programRepository.downloadProgram(programId)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }

    companion object {
    }
}