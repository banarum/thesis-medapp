package com.koenigmed.luomanager.data.mapper.user

import com.koenigmed.luomanager.data.model.JsonUserData
import com.koenigmed.luomanager.data.room.entity.UserDataEntity
import com.koenigmed.luomanager.domain.model.UserData
import com.koenigmed.luomanager.extension.removeMeasure
import com.koenigmed.luomanager.presentation.mvp.profile_edit.UserDataPresentation
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun mapToModel(jsonUserData: JsonUserData): UserData =
            UserData(
                    jsonUserData.email.orEmpty(),
                    jsonUserData.name.orEmpty(),
                    jsonUserData.surname.orEmpty(),
                    jsonUserData.birthDate,
                    jsonUserData.height,
                    jsonUserData.weight
            )

    fun mapToModel(userData: UserDataPresentation): UserData =
            UserData(
                    userData.email,
                    userData.name,
                    userData.surname,
                    userData.birthDate,
                    (userData.height.removeMeasure()).toInt(),
                    (userData.weight.removeMeasure()).toInt()
            )

    fun mapToJson(userData: UserData) =
            mapOf(
                    "email" to userData.email,
                    "password" to null,
                    "firstName" to userData.name,
                    "lastName" to userData.surname,
                    "birthdate" to userData.birthDate,
                    "heightCm" to userData.height,
                    "weightKg" to userData.weight
            )

    fun mapToPresentation(userData: UserData) =
            UserDataPresentation(
                    userData.email,
                    userData.name,
                    userData.surname,
                    userData.birthDate,
                    userData.height?.toString().orEmpty(),
                    userData.weight?.toString().orEmpty())

    fun mapToEntity(userData: UserData) =
            UserDataEntity(
                    userData.email,
                    userData.name,
                    userData.surname,
                    userData.birthDate,
                    userData.height,
                    userData.weight)

    fun mapToModel(userData: UserDataEntity): UserData =
            UserData(
                    userData.email,
                    userData.name,
                    userData.surname,
                    userData.birthDate,
                    userData.height,
                    userData.weight
            )
}