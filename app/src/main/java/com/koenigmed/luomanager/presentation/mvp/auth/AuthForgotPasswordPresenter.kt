package com.koenigmed.luomanager.presentation.mvp.auth

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.util.isValidEmail
import javax.inject.Inject

@InjectViewState
class AuthForgotPasswordPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val authInteractor: AuthInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<AuthForgotPasswordView>() {

    fun send(email: String) {
        if (!isValidEmail(email)) {
            viewState.showMessage(resourceManager.getString(R.string.error_invalid_email))
            return
        }
        authInteractor.sendPassword(email)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe { viewState.showProgress(true) }
                .doAfterTerminate { viewState.showProgress(false) }
                .subscribe(
                        {
                            viewState.showMessage(resourceManager.getString(R.string.auth_password_sent))
                            router.exit()
                        },
                        { errorHandler.proceed(it, { viewState.showMessage(it) }) }
                ).connect()
    }

    fun onBackPressed() = router.exit()
}