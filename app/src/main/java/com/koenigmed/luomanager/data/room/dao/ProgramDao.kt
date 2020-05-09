package com.koenigmed.luomanager.data.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.koenigmed.luomanager.data.room.entity.CurrentStateEntity
import com.koenigmed.luomanager.data.room.entity.MyoProgramEntity
import com.koenigmed.luomanager.data.room.entity.MyoProgramHistoryEntity
import org.threeten.bp.LocalDateTime

@Dao
interface ProgramDao {

    @Query("SELECT * FROM myo_program WHERE createdByUser = 0")
    fun getPrograms(): List<MyoProgramEntity>?

    @Query("SELECT * FROM myo_program WHERE id = :programId LIMIT 1")
    fun getProgram(programId: Int): MyoProgramEntity?

    @Query("SELECT * FROM myo_program WHERE createdByUser = 1")
    fun getUserCreatedPrograms(): List<MyoProgramEntity>?

    @Query("DELETE FROM myo_program WHERE createdByUser = 1 AND id = :programId")
    fun deleteUserCreatedProgram(programId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProgram(program: MyoProgramEntity): Long

    @Query("SELECT pr.* FROM myo_program pr INNER JOIN current_state ON pr.id = current_state.selectedProgramId")
    fun getSelectedProgram(): MyoProgramEntity?

    @Query("SELECT selectedProgramId FROM current_state")
    fun getSelectedProgramId(): Long?

    @Query("UPDATE current_state SET selectedProgramId = :programId")
    fun setSelectedProgramId(programId: Int?)

    @Query("UPDATE current_state SET historyRefreshDate = :date")
    fun setHistoryRefreshDate(date: LocalDateTime?)

    @Query("SELECT * FROM current_state")
    fun getCurrentState(): CurrentStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun initCurrentState(currentState: CurrentStateEntity)

    @Query("SELECT * FROM myo_history")
    fun getHistory() : List<MyoProgramHistoryEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryItem(program: MyoProgramHistoryEntity): Long

    @Query("DELETE FROM myo_history")
    fun deleteHistory()

}