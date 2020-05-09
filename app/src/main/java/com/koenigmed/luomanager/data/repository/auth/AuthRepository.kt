package com.koenigmed.luomanager.data.repository.auth

import com.koenigmed.luomanager.data.repository.prefs.IPrefsRepository
import com.koenigmed.luomanager.data.room.dao.UserDao
import com.koenigmed.luomanager.data.server.MyoApi
import com.koenigmed.luomanager.extension.removeMeasure
import com.koenigmed.luomanager.presentation.mvp.auth.RegisterPresentation
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.util.RxUtil
import javax.inject.Inject

class AuthRepository @Inject constructor(
        private val prefsRepository: IPrefsRepository,
        private val schedulers: SchedulersProvider,
        private val api: MyoApi,
        private val userDao: UserDao
) : IAuthRepository {

    override val isSignedIn get() = prefsRepository.token.isNotEmpty()

    override fun requestOAuthToken(username: String, password: String) =
            api.auth(
                    mapOf(
                            "username" to username,
                            "password" to password
                    ))
                    .map { it.token }

    override fun sendPassword(email: String) = api.resetPassword(
            mapOf("email" to email)
    )
            .map { RxUtil.Irrelevant.INSTANCE }

    override fun saveAuthData(token: String) {
        prefsRepository.token = token
    }

    override fun logout() {
        api.logout()
                .doOnSuccess { userDao.deleteUserData() }
                .subscribeOn(schedulers.io())
                .subscribe({ prefsRepository.logout() }, { it.printStackTrace() })
    }

    override fun register(registerPresentation: RegisterPresentation) =
            api.register(
                    mapOf(
                            "email" to registerPresentation.email,
                            "password" to registerPresentation.password,
                            "gender" to registerPresentation.gender,
                            "firstname" to registerPresentation.name,
                            "lastname" to registerPresentation.surname,
                            "username" to registerPresentation.email,
                            "birthdate" to registerPresentation.birthDate,
                            "heightCm" to registerPresentation.height.removeMeasure().toInt(),
                            "weightKg" to registerPresentation.weight.removeMeasure().toInt()
                    )
            )
                    .flatMap { requestOAuthToken(registerPresentation.email, registerPresentation.password) }
}