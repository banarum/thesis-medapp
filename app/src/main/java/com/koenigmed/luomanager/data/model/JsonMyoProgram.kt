package com.koenigmed.luomanager.data.model
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalTime


data class JsonMyoProgram(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("executionMethodId") val executionMethodId: Int?,
        @SerializedName("executionTimeS") val executionTimeS: Long?,
        @SerializedName("recommendation") val recommendation: String?,
        @SerializedName("recommendationFull") val recommendationFull: String?,
        @SerializedName("startTimes") val startTimes: List<LocalTime>?,
        @SerializedName("programType") val programType: Int?,
        @SerializedName("executionMethod") val executionMethod: JsonExecutionMethod?,
        @SerializedName("myoProgramMyoTask") val myoProgramMyoTaskList: List<JsonMyoProgramMyoTask?>?,
        @SerializedName("prescription") val prescription: List<Any?>?
)

data class JsonExecutionMethod(
        @SerializedName("id") val id: Int,
        @SerializedName("modeName") val modeName: String?
)

data class JsonMyoProgramMyoTask(
    @SerializedName("id") val id: Int,
    @SerializedName("myoTaskId") val myoTaskId: Int?,
    @SerializedName("myoProgramId") val myoProgramId: Int?,
    @SerializedName("executionOrder") val executionOrder: Int?,
    @SerializedName("executionTimeS") val executionTimeS: Long?,
    @SerializedName("channelNumber") val channelNumber: Int?,
    @SerializedName("myoTask") val myoTask: JsonMyoTask?
)

data class JsonMyoTask(
        @SerializedName("id") val id: Int?,
        @SerializedName("name") val name: String?,
        @SerializedName("current") val current: Int?,
        @SerializedName("waveFormId") val waveFormId: Int?,
        @SerializedName("burstMs") val burstMs: Long?,
        @SerializedName("pauseMs") val pauseMs: Long?,
        @SerializedName("pulseMs") val pulseMs: Long?,
        @SerializedName("bipolar") val bipolar: Boolean?
)

data class WaveForm(
    @SerializedName("id") val id: Int?,
    @SerializedName("impulseFormName") val impulseFormName: String?
)
