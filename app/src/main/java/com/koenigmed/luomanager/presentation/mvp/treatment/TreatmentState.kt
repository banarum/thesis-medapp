package com.koenigmed.luomanager.presentation.mvp.treatment

import com.koenigmed.luomanager.extension.getTimerString
import org.threeten.bp.LocalTime

sealed class TreatmentState {
    sealed class Manual : TreatmentState() {
        object Idle : Manual()
        data class Running(var timeRunning: LocalTime = LocalTime.of(0, 0, 0)) : Manual() {
            fun plusSecond() {
                timeRunning = timeRunning.plusSeconds(1)
            }

            fun getTimeRunningString() = timeRunning.getTimerString()
        }

        fun toggle(): TreatmentState.Manual {
            return when (this) {
                is Idle -> Running()
                is Running -> Idle
                else -> throw IllegalStateException("Trying to change state while NO_DEVICE_CONNECTION")
            }
        }

    }
    class Schedule(val schedule: List<LocalTime>) : TreatmentState()
    class NoDeviceConnection : TreatmentState()

}
