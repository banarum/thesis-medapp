package com.koenigmed.luomanager.presentation.mvp.program

import com.koenigmed.luomanager.domain.model.program.ProgramType
import org.threeten.bp.LocalTime

data class MyoProgramPresentation(
        var id: String,
        var name: String,
        var isSelected: Boolean,
        val startTimes: List<LocalTime>?,
        var programType: ProgramType,
        var createdByUser: Boolean
) {
}