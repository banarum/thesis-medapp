package com.koenigmed.luomanager.presentation.global

import com.jakewharton.rxrelay2.PublishRelay
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.exception.ServerError
import com.koenigmed.luomanager.extension.userMessage
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ErrorHandler @Inject constructor(
        private val router: FlowRouter,
        private val authInteractor: AuthInteractor,
        private val resourceManager: IResourceManager,
        private val schedulers: SchedulersProvider
) {

    private val authErrorRelay = PublishRelay.create<Boolean>()

    init {
        subscribeOnAuthErrors()
    }

    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {
        Timber.e(error)
        if (error is ServerError) {
            when (error.errorCode) {
                401 -> authErrorRelay.accept(true)
                else -> messageListener(error.userMessage(resourceManager))
            }
        } else {
            messageListener(error.userMessage(resourceManager))
        }
    }

    private fun subscribeOnAuthErrors() {
        authErrorRelay
                .throttleFirst(50, TimeUnit.MILLISECONDS)
                .observeOn(schedulers.ui())
                .subscribe { logout() }
    }

    private fun logout() {
    }
}
