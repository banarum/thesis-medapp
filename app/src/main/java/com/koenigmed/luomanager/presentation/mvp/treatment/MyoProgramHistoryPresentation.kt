package com.koenigmed.luomanager.presentation.mvp.treatment

import org.threeten.bp.LocalDateTime

data class MyoProgramHistoryPresentation(
        var id: Int?,
        var name: String,
        var startTime: LocalDateTime,
        var executionTimeS: Long
)