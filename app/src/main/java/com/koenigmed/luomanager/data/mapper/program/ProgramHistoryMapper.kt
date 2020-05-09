package com.koenigmed.luomanager.data.mapper.program

import com.koenigmed.luomanager.data.room.entity.MyoProgramHistoryEntity
import com.koenigmed.luomanager.domain.model.program.MyoProgramHistory
import com.koenigmed.luomanager.presentation.mvp.treatment.MyoProgramHistoryPresentation
import javax.inject.Inject

class ProgramHistoryMapper @Inject constructor() {
    fun mapToHistory(item: MyoProgramHistoryEntity) = MyoProgramHistory(
            item.id!!,
            item.name,
            item.startTime,
            item.executionTimeS
    )

    fun mapToEntity(item: MyoProgramHistory) = MyoProgramHistoryEntity(
            item.id,
            item.name,
            item.startTime,
            item.executionTimeS
    )

    fun mapToPresentation(item: MyoProgramHistory) = MyoProgramHistoryPresentation(
            item.id,
            item.name,
            item.startTime,
            item.executionTimeS
    )
}