package com.koenigmed.luomanager.data.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.koenigmed.luomanager.data.room.entity.UserDataEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putUserData(userData: UserDataEntity)

    @Query("SELECT * FROM user LIMIT 1")
    fun getUserData(): UserDataEntity?

    @Query("DELETE FROM user")
    fun deleteUserData()
}