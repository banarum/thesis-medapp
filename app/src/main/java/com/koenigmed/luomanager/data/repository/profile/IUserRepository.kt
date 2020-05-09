package com.koenigmed.luomanager.data.repository.profile

import com.koenigmed.luomanager.domain.model.UserData
import com.koenigmed.luomanager.util.RxUtil
import io.reactivex.Single

interface IUserRepository {
    fun save(userData: UserData): Single<RxUtil.Irrelevant>
    fun getUserData(): Single<UserData>
}