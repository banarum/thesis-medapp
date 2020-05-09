package com.koenigmed.luomanager.toothpick.provider.db

import android.arch.persistence.room.Room
import android.content.Context
import com.koenigmed.luomanager.data.room.AppDatabase
import toothpick.ProvidesSingletonInScope
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
internal class AppDatabaseProvider : Provider<AppDatabase> {

    @Inject
    lateinit var context: Context

    override fun get(): AppDatabase {
        return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, AppDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

}