package com.koenigmed.luomanager.domain.model.program

import org.threeten.bp.LocalTime


data class MyoProgram(
        var id: String? = null,
        var name: String,
        var executionMethodId: Int,
        var executionTimeS: Long,
        var recommendation: String,
        var recommendationFull: String,
        val startTimes: List<LocalTime>?,
        val programType: ProgramType,
        val myoProgramMyoTaskList: List<MyoProgramMyoTask>?,
        var createdByUser: Boolean
)

data class MyoProgramMyoTask(
        val id: Int?,
        val myoTaskId: Int?,
        val myoProgramId: Int?,
        val executionOrder: Int?,
        val executionTimeS: Long?,
        val channelNumber: Int?,
        val myoTask: MyoTask?
)

data class MyoTask(
        val id: Int?,
        val name: String,
        val current: Int?,
        val waveFormId: Int?,
        val burstMs: Long?,
        val pauseMs: Long?,
        val pulseMs: Double?,
        val bipolar: Boolean?
)

data class ExecutionMethod(
        val id: Int,
        val modeName: String?
)