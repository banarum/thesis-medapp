package com.koenigmed.luomanager.data.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import org.threeten.bp.LocalDate


@Entity(tableName = "user")
data class UserDataEntity(
        var email: String = "",
        @PrimaryKey()
        @NotNull
        var name: String = "",
        var surname: String = "",
        var birthDate: LocalDate?,
        var height: Int?,
        var weight: Int?
)