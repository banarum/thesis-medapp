package com.koenigmed.luomanager.data.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import org.threeten.bp.LocalDateTime

@Entity(tableName = "myo_history")
data class MyoProgramHistoryEntity(
        @PrimaryKey(autoGenerate = true)
        @NotNull
        var id: Int? = null,
        var name: String,
        var startTime: LocalDateTime,
        var executionTimeS: Long
)