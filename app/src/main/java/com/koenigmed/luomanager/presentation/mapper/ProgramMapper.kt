package com.koenigmed.luomanager.presentation.mapper

import com.koenigmed.luomanager.domain.model.program.*
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.presentation.mvp.receipt.ChannelData
import com.koenigmed.luomanager.presentation.mvp.receipt.DownloadStatus
import com.koenigmed.luomanager.presentation.mvp.receipt.MyoProgramDownloadPresentation
import com.koenigmed.luomanager.presentation.mvp.receipt.ReceiptPresentation
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class ProgramMapper @Inject constructor() {

    fun mapToPresentation(program: MyoProgram, selectedProgram: MyoProgram? = null): MyoProgramPresentation {
        return MyoProgramPresentation(
                program.id!!,
                program.name,
                program.id == selectedProgram?.id,
                program.startTimes,
                program.programType,
                program.createdByUser)
    }

    fun mapToReceipt(program: MyoProgram, selectedProgram: MyoProgram? = null): MyoProgramPresentation {
        return MyoProgramPresentation(
                program.id!!,
                program.name,
                program.id == selectedProgram?.id,
                program.startTimes,
                program.programType,
                program.createdByUser)
    }


    fun mapToProgram(program: ReceiptPresentation): MyoProgram {
        return MyoProgram(
                id = null,
                name = program.name,
                executionMethodId = 1,
                executionTimeS = program.executionTimeS,
                recommendation = "",
                recommendationFull = "",
                startTimes = program.getSchedule(),
                programType = program.programType,
                myoProgramMyoTaskList = mapToTaskList(program),
                createdByUser = true
        )
    }

    private fun mapToTaskList(program: ReceiptPresentation): List<MyoProgramMyoTask> {
        val channel1Data = program.channel1Data
        val channel2Data = program.channel2Data
        val result = mutableListOf<MyoProgramMyoTask>()
        if (channel1Data?.isEnabled == true) result.add(mapToTask(program.executionTimeS, channel1Data, 1))
        if (channel2Data?.isEnabled == true) result.add(mapToTask(program.executionTimeS, channel2Data, 2))
        return result
    }

    private fun mapToChannelData(channelId: Int, tasks: List<MyoProgramMyoTask>) : ChannelData{
        val result = ChannelData()
        for (task in tasks){
            if (task.channelNumber == channelId) {
                result.amperage = task.myoTask!!.current!!
                result.bipolar = task.myoTask.bipolar!!
                result.durationMs = task.myoTask.burstMs!!
                result.frequency = (task.myoTask.pulseMs!! / 1_000).toInt()
                result.pulseForm = PulseForm(task.myoTask.waveFormId!!, "")
                break
            }
        }
        return result
    }

    private fun mapToTask(executionTimeS: Long, channel: ChannelData, channelNumber: Int): MyoProgramMyoTask {
        return MyoProgramMyoTask(
                null,
                null,
                null,
                null,
                executionTimeS,
                channelNumber,
                MyoTask(
                        id = null,
                        name = "",
                        current = channel.amperage,
                        waveFormId = channel.pulseForm!!.id,
                        burstMs = channel.durationMs,
                        pauseMs = channel.durationMs,
                        pulseMs = (1.0 / channel.frequency.toDouble()) * 1_000,
                        bipolar = channel.bipolar
                )
        )
    }

    fun mapToDownloadPresentation(program: MyoProgram): MyoProgramDownloadPresentation =
            MyoProgramDownloadPresentation(program.id!!, program.name, DownloadStatus.NOT_DOWNLOADED)

    fun mapToHistory(program: MyoProgram, dateTime: LocalDateTime) =
            MyoProgramHistory(
                    null,
                    program.name,
                    dateTime,
                    program.executionTimeS
            )
}