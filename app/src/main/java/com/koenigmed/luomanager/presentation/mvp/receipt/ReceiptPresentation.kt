package com.koenigmed.luomanager.presentation.mvp.receipt

import com.koenigmed.luomanager.domain.model.program.ProgramType
import com.koenigmed.luomanager.domain.model.program.PulseForm
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import timber.log.Timber
import java.util.concurrent.TimeUnit


val DEFAULT_EXECUTION_TIME_S = TimeUnit.MINUTES.toSeconds(15)

class ReceiptPresentation(
) {
    var name: String = ""
    var programType: ProgramType = ProgramType.LITE
    var executionTimeS: Long = DEFAULT_EXECUTION_TIME_S
    var startTime: LocalTime = LocalTime.of(8, 0, 0)
    var endTime: LocalTime = LocalTime.of(20, 0, 0)
    var channel1Data: ChannelData? = null
    var channel2Data: ChannelData? = null

    fun isValid() = name.isNotBlank() && channel1Data?.isValid() == true && channel2Data?.isValid() == true

    fun getDurationSec() = 0L

    fun getSchedule(): List<LocalTime>? {
        if (programType == ProgramType.MANUAL) return null
        Timber.d("startTime " + startTime)
        Timber.d("endTime " + endTime)
        val timesPerDay = when (programType.number) {
            2 -> 3
            3 -> 5
            4 -> 8
            else -> 3
        }
        val minutesToAdd = Duration.between(startTime, endTime).toMinutes() / timesPerDay
        val result = mutableListOf<LocalTime>()
        for (i in 1..timesPerDay) {
            result.add(startTime.plusMinutes(i * minutesToAdd))
        }
        return result
    }

}

data class ChannelData(
        var isEnabled: Boolean = false,
        var pulseForm: PulseForm? = null,
        var bipolar: Boolean = false,
        var amperage: Int = 230,
        var durationMs: Long = 10,
        var frequency: Int = 10
) {
    fun isValid(): Boolean {
        if (!isEnabled) {
            return true
        }
        return pulseForm != null
    }
}