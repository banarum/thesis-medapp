package com.koenigmed.luomanager.data.repository.profile

import com.koenigmed.luomanager.data.mapper.user.UserMapper
import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.data.room.dao.UserDao
import com.koenigmed.luomanager.data.server.MyoApi
import com.koenigmed.luomanager.domain.model.UserData
import com.koenigmed.luomanager.util.RxUtil
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor(
        private val prefsRepository: IPrefsRepository,
        private val userMapper: UserMapper,
        private val userDao: UserDao,
        private val api: MyoApi
) : IUserRepository {

    override fun getUserData(): Single<UserData> =
            api.getUserData()
                    .map { userMapper.mapToModel(it) }
                    .doOnSuccess { userDao.putUserData(userMapper.mapToEntity(it)) }
                    .doOnError { Timber.e(it) }
                    .onErrorResumeNext {
                        Single.fromCallable {
                            userMapper.mapToModel(userDao.getUserData()!!)
                        }
                    }

    override fun save(userData: UserData) =
            api.setUserData(userMapper.mapToJson(userData))
                    .map { RxUtil.Irrelevant.INSTANCE }
}