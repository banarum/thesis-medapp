package com.koenigmed.luomanager.data.repository.program

import com.koenigmed.luomanager.domain.model.program.MyoProgram
import com.koenigmed.luomanager.domain.model.program.MyoProgramHistory
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.domain.model.program.Receipt
import com.koenigmed.luomanager.util.Optional
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime

interface IProgramRepository {
    fun getPrograms() : Single<List<MyoProgram>>
    fun getProgramsFromNetwork() : Single<List<MyoProgram>>
    fun getUserCreatedPrograms() : Single<List<MyoProgram>>
    fun downloadProgram(programId: String): Single<Any>
    fun getPulseForms(): Single<List<PulseForm>>
    fun getReceipts(): Single<List<Receipt>>
    fun getSelectedProgram(): Single<MyoProgram>
    fun setSelectedProgram(programId: String): Completable
    fun deleteUserProgram(programId: String): Completable
    fun saveReceipt(program: MyoProgram): Completable
    fun getHistory(): Single<List<MyoProgramHistory>>
    fun getProgram(programId: String): Single<MyoProgram>
    fun getHistoryRefreshDate(): Single<Optional<LocalDateTime>>
    fun addHistoryItem(history: MyoProgramHistory) : Completable
    fun setHistoryRefreshDate(date: LocalDateTime?)
}