package com.koenigmed.luomanager.data.decice

import com.google.gson.annotations.SerializedName
import com.koenigmed.luomanager.R

data class DeviceResponse(
        @SerializedName("errorCode")
        val error : Error
)

enum class Error(val messageResId: Int) {
    @SerializedName("1")
    ERROR_1(R.string.device_error_1),
    @SerializedName("2")
    ERROR_2(R.string.device_error_2),
    @SerializedName("3")
    ERROR_3(R.string.device_error_3),
    @SerializedName("4")
    ERROR_4(R.string.device_error_4),
    @SerializedName("5")
    ERROR_5(R.string.device_error_5),
    @SerializedName("6")
    ERROR_6(R.string.device_error_6),
    @SerializedName("10")
    ERROR_10(R.string.device_error_10)
}