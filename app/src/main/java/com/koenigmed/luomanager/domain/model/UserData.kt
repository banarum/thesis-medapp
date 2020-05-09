package com.koenigmed.luomanager.domain.model

import org.threeten.bp.LocalDate


data class UserData(
        var email: String = "",
        var name: String = "",
        var surname: String = "", var birthDate: LocalDate?,
        var height: Int?, var weight: Int?
)