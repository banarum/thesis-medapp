package com.koenigmed.luomanager.presentation.mvp.program

import com.koenigmed.luomanager.presentation.mvp.receipt.DownloadStatus
import com.koenigmed.luomanager.presentation.mvp.receipt.MyoProgramDownloadPresentation

data class DownloadProgramsPresentation(
        var programs: List<MyoProgramDownloadPresentation>) {

    fun changeProgramStatus(programId: String, status: DownloadStatus) {
        programs.forEach {
            if (it.id == programId) {
                it.downloadStatus = status
                return
            }
        }
    }

}