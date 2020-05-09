package com.koenigmed.luomanager.presentation.mvp.auth

import com.koenigmed.luomanager.extension.getAge
import org.threeten.bp.LocalDate

data class RegisterPresentation(var email: String = "", var password: String = "", var name: String = "",
                                var surname: String = "", var gender: String = "", var birthDate: LocalDate? = null,
                                var height: String = "", var weight: String = "") {

    fun isReady(): Boolean = email.isNotBlank() && password.isNotBlank() && name.isNotBlank()
            && birthDate != null

    fun getAge(): String = birthDate?.getAge() ?:""
}