package com.koenigmed.luomanager.data.model.device

import com.google.gson.annotations.SerializedName

open class DeviceCommand(
        @SerializedName("command")
        val command: String
)