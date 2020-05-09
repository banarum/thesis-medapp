package com.koenigmed.luomanager.data.model.device

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime

class SyncTimeCommand(
        @SerializedName("datetime")
        val dateTime: LocalDateTime
) : DeviceCommand("setTime")