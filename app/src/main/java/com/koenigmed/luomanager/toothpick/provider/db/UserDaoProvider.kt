package com.koenigmed.luomanager.toothpick.provider.db

import com.koenigmed.luomanager.data.room.AppDatabase
import com.koenigmed.luomanager.data.room.dao.UserDao
import toothpick.ProvidesSingletonInScope
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
internal class UserDaoProvider : Provider<UserDao> {

    @Inject
    lateinit var db: AppDatabase

    override fun get(): UserDao {
        return db.userDao()
    }

}