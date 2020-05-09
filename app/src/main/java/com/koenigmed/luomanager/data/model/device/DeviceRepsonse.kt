package com.koenigmed.luomanager.data.model.device

import com.google.gson.annotations.SerializedName

data class JsonDeviceResponse(
        @SerializedName("response") val response: String?,
        @SerializedName("errCode") val errorCode: Int?
) {
    fun isOk() = response == "ok"
}