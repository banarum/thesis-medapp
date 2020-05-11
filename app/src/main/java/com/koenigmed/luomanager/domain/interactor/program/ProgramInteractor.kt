package com.koenigmed.luomanager.domain.interactor.program

import com.google.gson.internal.LinkedTreeMap
import com.koenigmed.luomanager.data.mapper.program.ProgramHistoryMapper
import com.koenigmed.luomanager.data.repository.program.IProgramRepository
import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.domain.model.program.MyoProgramHistory
import com.koenigmed.luomanager.presentation.mapper.ProgramMapper
import com.koenigmed.luomanager.presentation.mvp.program.ProgramsPresentation
import com.koenigmed.luomanager.presentation.mvp.treatment.MyoProgramHistoryPresentation
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.util.Optional
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

class ProgramInteractor
@Inject constructor(private val programRepository: IProgramRepository,
                    private val programMapper: ProgramMapper,
                    private val historyMapper: ProgramHistoryMapper,
                    private val schedulers: SchedulersProvider) {
    fun getAllPrograms(query: String? = null) =
            Single.zip(programRepository.getPrograms(), programRepository.getUserCreatedPrograms(), programRepository.getSelectedProgram(),
                    Function3<List<MyoProgram>, List<MyoProgram>, MyoProgram, ProgramsPresentation>
                    { programs, userPrograms, selectedProgram ->
                        ProgramsPresentation(programs.map { programMapper.mapToPresentation(it, selectedProgram) },
                                userPrograms.map { programMapper.mapToPresentation(it, selectedProgram) }, query)
                    })
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())

    fun getSelectedProgram() = programRepository.getSelectedProgram()
            .map { program -> programMapper.mapToPresentation(program, program) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun toggleSelectedProgram(programId: String) = programRepository.setSelectedProgram(programId)
            .subscribeOn(schedulers.io())

    fun deletePrograms(programIdsToDelete: MutableSet<String>) =
            Completable.concat(
                    programIdsToDelete.map { programId ->
                        programRepository.deleteUserProgram(programId)
                    }
            )
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())

    fun getHistory(): Single<LinkedTreeMap<LocalDate, List<MyoProgramHistoryPresentation>>> {
        return Single.zip(programRepository.getHistory(), programRepository.getHistoryRefreshDate(),
                BiFunction<List<MyoProgramHistory>, Optional<LocalDateTime>, LinkedTreeMap<LocalDate, List<MyoProgramHistoryPresentation>>> { history, dateOpt ->
                    if (!dateOpt.isPresent) return@BiFunction LinkedTreeMap()
                    val refreshDate = dateOpt.get()!!
                    val map = LinkedTreeMap<LocalDate, List<MyoProgramHistoryPresentation>>()

                    history.forEach {
                        val key = it.startTime.toLocalDate()
                        val list = map[key].orEmpty().toMutableList()
                        list.add(historyMapper.mapToPresentation(it))
                        map[key] = list
                    }

                    val selectedProgram = programRepository.getSelectedProgram().blockingGet()
                    val now = LocalDateTime.now()
                    var date = refreshDate
                    while (date.isBefore(now)) {
                        val list = map[date.toLocalDate()].orEmpty().toMutableList()
                        selectedProgram.startTimes?.forEach { startTime ->
                            val dateTime = LocalDateTime.of(date.toLocalDate(), startTime)
                            if (dateTime.isAfter(refreshDate) && dateTime.isBefore(now)) {
                                val historyItem = programMapper.mapToHistory(selectedProgram, dateTime)
                                programRepository.addHistoryItem(historyItem)
                                        .subscribeOn(schedulers.io())
                                        .subscribe({}, { Timber.e(it) })
                                list.add(historyMapper.mapToPresentation(historyItem))
                            }
                        }
                        map[date.toLocalDate()] = list
                        date = date.plusDays(1)
                    }
                    programRepository.setHistoryRefreshDate(now)
                    map
                })
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }

    fun getProgram(programId: String) = programRepository.getProgram(programId)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    fun getProgramPresentation(programId: String) = programRepository.getProgram(programId)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .map {programMapper.mapToPresentation(it)}

    fun initPrograms() {} // must be empty
}