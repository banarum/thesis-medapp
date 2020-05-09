package com.koenigmed.luomanager.presentation.mvp.profile_edit

import com.koenigmed.luomanager.extension.getAge
import org.threeten.bp.LocalDate

data class UserDataPresentation(
        var email: String = "",
        var name: String = "",
        var surname: String = "", var birthDate: LocalDate? = null,
        var height: String = "", var weight: String = ""
) {

    fun getUsername() = "$name $surname"

    fun isReady(): Boolean = name.isNotBlank() && surname.isNotBlank()
            && birthDate != null && height.isNotBlank()
            && weight.isNotBlank()

    fun getAgeString() = birthDate?.getAge() ?:""
}