package com.koenigmed.luomanager.data.room.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "current_state")
class CurrentStateEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    var selectedProgramId: Int? = null
    var historyRefreshDate: LocalDateTime? = null

}