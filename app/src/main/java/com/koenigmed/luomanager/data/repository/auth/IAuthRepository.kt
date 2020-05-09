package com.koenigmed.luomanager.data.repository.auth

import com.koenigmed.luomanager.presentation.mvp.auth.RegisterPresentation
import com.koenigmed.luomanager.util.RxUtil
import io.reactivex.Single

interface IAuthRepository {
    val isSignedIn: Boolean
    fun requestOAuthToken(username: String, password: String): Single<String>
    fun sendPassword(email: String): Single<RxUtil.Irrelevant>
    fun saveAuthData(token: String)
    fun logout()
    fun register(registerPresentation: RegisterPresentation): Single<String>
}