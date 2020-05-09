package com.koenigmed.luomanager.data.model

import com.google.gson.annotations.SerializedName

data class JsonTokenData(
        @SerializedName("token") val token: String,
        @SerializedName("token_type") val type: String?,
        @SerializedName("scope") val scope: String?,
        @SerializedName("created_at") val createdAt: Long?,
        @SerializedName("refresh_token") val refreshToken: String?
)