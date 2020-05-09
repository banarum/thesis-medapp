package com.koenigmed.luomanager.domain.model.program

import org.threeten.bp.LocalDateTime

data class MyoProgramHistory(
        var id: Int? = null,
        var name: String,
        var startTime: LocalDateTime,
        var executionTimeS: Long
)