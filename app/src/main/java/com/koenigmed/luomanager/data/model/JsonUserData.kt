package com.koenigmed.luomanager.data.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate

data class JsonUserData(
        @SerializedName("email") var email: String? = "",
        @SerializedName("firstName") var name: String? = "",
        @SerializedName("lastName") var surname: String? = "",
        @SerializedName("birthdate") var birthDate: LocalDate?,
        @SerializedName("heightCm") var height: Int?,
        @SerializedName("weightKg") var weight: Int?
)