package com.koenigmed.luomanager.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.koenigmed.luomanager.data.room.converter.MyoTypeConverters
import com.koenigmed.luomanager.data.room.converter.RegularTypeConverters
import com.koenigmed.luomanager.data.room.dao.ProgramDao
import com.koenigmed.luomanager.data.room.dao.UserDao
import com.koenigmed.luomanager.data.room.entity.CurrentStateEntity
import com.koenigmed.luomanager.data.room.entity.MyoProgramEntity
import com.koenigmed.luomanager.data.room.entity.MyoProgramHistoryEntity
import com.koenigmed.luomanager.data.room.entity.UserDataEntity

@Database(entities = [MyoProgramEntity::class, CurrentStateEntity::class, UserDataEntity::class,
    MyoProgramHistoryEntity::class], version = 1)
@TypeConverters(MyoTypeConverters::class, RegularTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun programsDao(): ProgramDao
    abstract fun userDao(): UserDao

    companion object {
        const val DB_NAME = "myo_database"
    }
}
