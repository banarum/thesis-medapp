package com.koenigmed.luomanager.data.mapper.program

import com.koenigmed.luomanager.data.model.*
import com.koenigmed.luomanager.data.model.device.ProgramCommand
import com.koenigmed.luomanager.data.room.entity.MyoProgramEntity
import com.koenigmed.luomanager.domain.model.program.*
import javax.inject.Inject

class ProgramMapper @Inject constructor() {

    fun mapToProgram(program: JsonMyoProgram): MyoProgram {
        return MyoProgram(
                program.id,
                program.name,
                program.executionMethodId!!,
                program.executionTimeS!!,
                program.recommendation.orEmpty(),
                program.recommendationFull.orEmpty(),
                program.startTimes,
                ProgramType.fromNumber(program.programType),
                program.myoProgramMyoTaskList.orEmpty().map { mapToMyoProgramMyoTask(it!!) },
                false)
    }

    fun mapToProgram(program: MyoProgramEntity): MyoProgram {
        return MyoProgram(
                program.id.toString(),
                program.name,
                program.executionMethodId,
                program.executionTimeS,
                program.recommendation,
                program.recommendationFull,
                program.startTimes,
                ProgramType.fromNumber(program.programType),
                program.myoProgramMyoTaskList,
                program.createdByUser
        )
    }


    fun mapToProgramCommand(program: MyoProgram): ProgramCommand {
        return ProgramCommand(
                program.startTimes.orEmpty(),
                program.executionMethodId,
                if (program.myoProgramMyoTaskList != null) {
                    program.myoProgramMyoTaskList.map { task -> mapToCommandTask(task) }
                } else {
                    null
                }
        )
    }

    fun mapToEntity(program: MyoProgram): MyoProgramEntity {
        return MyoProgramEntity(
                program.id?.toInt(),
                program.name,
                program.executionMethodId,
                program.executionTimeS,
                program.recommendation,
                program.recommendationFull,
                program.startTimes,
                program.programType.number,
                program.myoProgramMyoTaskList,
                program.createdByUser
        )
    }

    private fun mapToMyoProgramMyoTask(task: JsonMyoProgramMyoTask): MyoProgramMyoTask {
        return MyoProgramMyoTask(
                task.id,
                task.myoTaskId,
                task.myoProgramId,
                task.executionOrder,
                task.executionTimeS,
                task.channelNumber,
                if (task.myoTask != null) mapToTask(task.myoTask) else null
        )
    }

    private fun mapToTask(task: JsonMyoTask): MyoTask {
        return MyoTask(
                task.id!!,
                task.name.orEmpty(),
                task.current,
                task.waveFormId,
                task.burstMs,
                task.pauseMs,
                task.pulseMs?.toDouble(),
                task.bipolar
        )
    }

    private fun mapToCommandTask(task: MyoProgramMyoTask): ProgramCommand.Task {
        return ProgramCommand.Task(
                task.executionTimeS!!,
                task.channelNumber,
                task.myoTask?.waveFormId!!,
                task.myoTask.pulseMs!!,
                task.myoTask.bipolar!!,
                task.myoTask.burstMs!!,
                task.myoTask.pauseMs!!,
                task.myoTask.current!!
        )
    }

    fun mapToExecutionMethod(executionMethod: JsonExecutionMethod): ExecutionMethod =
            ExecutionMethod(executionMethod.id, executionMethod.modeName.orEmpty())

    fun mapToPulseForm(jsonPulseForm: JsonPulseForm): PulseForm =
            PulseForm(jsonPulseForm.id, jsonPulseForm.impulseFormName)

    fun mapToPulseForm(waveForm: WaveForm): PulseForm =
            PulseForm(waveForm.id!!, waveForm.impulseFormName.orEmpty())
}