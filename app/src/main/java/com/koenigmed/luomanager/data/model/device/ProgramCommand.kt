package com.koenigmed.luomanager.data.model.device

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalTime

class ProgramCommand(
        @SerializedName("startTimes")
        val startTimes: List<LocalTime>,
        @SerializedName("executionMethod")
        val executionMethodId: Int,
        @SerializedName("tasks")
        var tasks: List<Task>?
) : DeviceCommand("program") {

    class Task(
            @SerializedName("duration")
            val durationSec: Long,
            @SerializedName("channelIds")
            val channelIds: Int?,
            @SerializedName("waveform")
            val waveFormId: Int,
            @SerializedName("tpulse")
            val tpulse: Double,    //      us
            @SerializedName("bipolar")
            val bipolar: Boolean,
            @SerializedName("tburst")
            val tburst: Long,    //      ms
            @SerializedName("tpause")
            val tpause: Long,    //      ms
            @SerializedName("current")
            var amperage: Int      //      mA
    )

}

