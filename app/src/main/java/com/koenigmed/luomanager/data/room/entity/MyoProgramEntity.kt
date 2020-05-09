package com.koenigmed.luomanager.data.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.koenigmed.luomanager.domain.model.program.MyoProgramMyoTask
import org.jetbrains.annotations.NotNull
import org.threeten.bp.LocalTime

@Entity(tableName = "myo_program")
data class MyoProgramEntity(
        @PrimaryKey(autoGenerate = true)
        @NotNull
        var id: Int? = null,
        var name: String,
        var executionMethodId: Int,
        var executionTimeS: Long,
        var recommendation: String,
        var recommendationFull: String,
        val startTimes: List<LocalTime>?,
        val programType: Int?,
        val myoProgramMyoTaskList: List<MyoProgramMyoTask>?,
        var createdByUser: Boolean
)