package com.koenigmed.luomanager.data.repository.program

import com.koenigmed.luomanager.data.mapper.program.ProgramHistoryMapper
import com.koenigmed.luomanager.data.mapper.program.ProgramMapper
import com.koenigmed.luomanager.data.model.JsonMyoProgram
import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.data.room.dao.ProgramDao
import com.koenigmed.luomanager.data.room.entity.CurrentStateEntity
import com.koenigmed.luomanager.data.server.MyoApi
import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.domain.model.program.MyoProgramHistory
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.domain.model.program.Receipt
import com.koenigmed.luomanager.util.AssetsHelper.listFromJsonFile
import com.koenigmed.luomanager.util.Optional
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

class ProgramRepository @Inject constructor(
        private val prefsRepository: IPrefsRepository,
        private val programMapper: ProgramMapper,
        private val programHistoryMapper: ProgramHistoryMapper,
        private val programDao: ProgramDao,
        private val api: MyoApi
) : IProgramRepository {

    private lateinit var hardcodedPrograms: List<JsonMyoProgram>

    private var pulseForms: MutableList<PulseForm>? = null
    private var receipts: MutableList<Receipt>? = null

    init {
        Timber.d("init")
        Single.just(programDao)
                .doOnSuccess { dao ->
                    val currentState = dao.getCurrentState()
                    if (currentState == null) {
                        dao.initCurrentState(CurrentStateEntity())
                        hardcodedPrograms = listFromJsonFile(javaClass, Array<JsonMyoProgram>::class.java,
                                HARDCODED_PROGRAMS_FILE_NAME)
                        hardcodedPrograms.map { programMapper.mapToProgram(it) }
                                .map { programMapper.mapToEntity(it) }
                                .filter { it.executionMethodId == 1 }
                                .take(3)
                                .forEach {
                                    val result = dao.insertProgram(it)
                                    Timber.d("result insertProgram $result")
                                }
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe({ }, { Timber.e(it) })
    }

    override fun getPrograms() = getProgramsFromDb()

    override fun getProgramsFromNetwork(): Single<List<MyoProgram>> {
        return api.getPrograms()
                .toObservable()
                .flatMapIterable { it }
                .filter { !FILTERED_PROGRAM_EXECUTION_METHOD_IDS.contains(it.executionMethodId) }
                .map { programMapper.mapToProgram(it) }
                .toList()
                .doOnSuccess { Timber.d("getProgramsToDownload here ") }
    }

    override fun addHistoryItem(history: MyoProgramHistory): Completable {
        return Completable.fromAction {
            programDao.insertHistoryItem(programHistoryMapper.mapToEntity(history))
        }
    }

    private fun getProgramsFromDb(): Single<List<MyoProgram>> {
        return Single.just(programDao)
                .map { dao -> dao.getPrograms() }
                .toObservable()
                .flatMapIterable { it }
                .map { programMapper.mapToProgram(it) }
                .toList()
    }

    override fun getSelectedProgram(): Single<MyoProgram> {
        return Single.just(programDao)
                .flatMap { dao ->
                    val selectedProgramId = dao.getSelectedProgramId()
                    getAllPrograms()
                            .toObservable()
                            .flatMapIterable { it }
                            .filter {
                                if (selectedProgramId == null) {
                                    dao.setSelectedProgramId(it.id?.toInt())
                                    true
                                } else {
                                    it.id == selectedProgramId.toString()
                                }
                            }
                            .firstOrError()
                }
                .doOnSuccess { programDao.setSelectedProgramId(it.id?.toInt()) }
    }

    private fun getAllPrograms() = Single.merge(getPrograms(), getUserCreatedPrograms())

    override fun getUserCreatedPrograms(): Single<List<MyoProgram>> {
        return Single.just(programDao)
                .map { dao -> dao.getUserCreatedPrograms() }
                .toObservable()
                .flatMapIterable { it }
                .map { programMapper.mapToProgram(it) }
                .toList()
                .doOnSuccess {
                    Timber.d("getUserCreatedPrograms here " + it)
                }
    }

    override fun setSelectedProgram(programId: String): Completable {
        return Completable.fromAction {
            programDao.setSelectedProgramId(programId.toInt())
            programDao.setHistoryRefreshDate(LocalDateTime.now())
        }
    }

    override fun saveReceipt(program: MyoProgram): Completable {
        return Completable.fromAction {
            programDao.insertProgram(programMapper.mapToEntity(program))
        }
    }

    override fun getProgram(programId: String): Single<MyoProgram> =
            Single.just(programDao)
                    .map { dao -> dao.getProgram(programId.toInt()) }
                    .map { programMapper.mapToProgram(it) }

    override fun getHistory(): Single<List<MyoProgramHistory>> =
            Single.just(programDao)
                    .map { dao -> dao.getHistory() }
                    .toObservable()
                    .flatMapIterable { it }
                    .map { programHistoryMapper.mapToHistory(it) }
                    .toList()

    override fun getHistoryRefreshDate(): Single<Optional<LocalDateTime>> =
            Single.just(programDao)
                    .map { dao -> Optional.of(dao.getCurrentState()!!.historyRefreshDate) }

    override fun setHistoryRefreshDate(date: LocalDateTime?) {
        programDao.setHistoryRefreshDate(date)
    }

    override fun downloadProgram(programId: String): Single<Any> =
            api.getProgram(programId.toInt())
                    .map { programMapper.mapToProgram(it) }
                    .map { programDao.insertProgram(programMapper.mapToEntity(it)) }

    override fun getPulseForms(): Single<List<PulseForm>> {
        if (pulseForms.orEmpty().isNotEmpty()) {
            return Single.just(pulseForms)
        }
        return api.getPulseForms()
                .toObservable()
                .flatMapIterable { it }
                .map { programMapper.mapToPulseForm(it) }
                .toList()
                .doOnSuccess {
                    pulseForms = it
                }
    }

    override fun deleteUserProgram(programId: String): Completable {
        return Completable.fromAction {
            programDao.deleteUserCreatedProgram(programId)
            if (programId == programDao.getSelectedProgramId().toString()) {
                programDao.setSelectedProgramId(null)
                programDao.setHistoryRefreshDate(null)
            }
        }
    }

    override fun getReceipts(): Single<List<Receipt>> {
        if (receipts.orEmpty().isNotEmpty()) {
            return Single.just(receipts)
        }
        return Single.just(listOf(Receipt(0, "receipt name")))
                .doOnSuccess {
                    receipts = it.toMutableList()
                }
    }

    companion object {
        val FILTERED_PROGRAM_EXECUTION_METHOD_IDS = setOf(2, 3)
        private const val HARDCODED_PROGRAMS_FILE_NAME = "hardcoded_programs.json"
    }
}