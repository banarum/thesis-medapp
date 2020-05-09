package com.koenigmed.luomanager.data.model

import com.google.gson.annotations.SerializedName

data class JsonPulseForm(
        @SerializedName("id") val id: Int,
        @SerializedName("impulseFormName") val impulseFormName: String
)