package com.koenigmed.luomanager.presentation.mvp.receipt

data class MyoProgramDownloadPresentation(
        var id: String,
        var name: String,
        var downloadStatus: DownloadStatus
)

enum class DownloadStatus {
    NOT_DOWNLOADED, IN_PROGRESS, COMPLETE
}
