package com.koenigmed.luomanager.domain.interactor.program

import com.koenigmed.luomanager.data.repository.program.IProgramRepository
import com.koenigmed.luomanager.system.SchedulersProvider
import javax.inject.Inject

class DownloadReceiptInteractor
@Inject constructor(private val programRepository: IProgramRepository,
                    private val schedulers: SchedulersProvider) {

    fun getReceipts() = programRepository.getReceipts()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())

    companion object {
    }
}