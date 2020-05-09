package com.koenigmed.luomanager.domain.interactor.auth

import com.koenigmed.luomanager.data.repository.auth.IAuthRepository
import com.koenigmed.luomanager.presentation.mvp.auth.RegisterPresentation
import io.reactivex.Completable
import javax.inject.Inject

class AuthInteractor
@Inject constructor(private val authRepository: IAuthRepository) {

    fun isSignedIn() = authRepository.isSignedIn

    fun login(username: String, password: String) =
            Completable.defer {
                authRepository.requestOAuthToken(username, password)
                        .doOnSuccess {
                            authRepository.saveAuthData(it)
                        }
                        .ignoreElement()
            }

    fun logout() {
        authRepository.logout()
    }

    fun sendPassword(email: String) =
            Completable.defer {
                authRepository.sendPassword(email)
                        .ignoreElement()
            }

    fun register(registerPresentation: RegisterPresentation) =
            Completable.defer {
                authRepository.register(registerPresentation)
                        .doOnSuccess {
                            authRepository.saveAuthData(it)
                        }
                        .ignoreElement()
            }

    companion object {
        private const val PARAMETER_CODE = "code"
    }
}