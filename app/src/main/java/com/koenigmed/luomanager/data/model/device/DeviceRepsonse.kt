package com.koenigmed.luomanager.data.model.device

import com.google.gson.annotations.SerializedName

data class JsonDeviceResponse(
        @SerializedName("response") val response: String?,
        @SerializedName("errCode") val errorCode: Int?,
        @SerializedName("voltage") val voltage: String? = null
) {
    fun isOk() = response == "ok"
}