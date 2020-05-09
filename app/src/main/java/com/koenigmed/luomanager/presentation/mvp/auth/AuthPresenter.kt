package com.koenigmed.luomanager.presentation.mvp.auth

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.auth.AuthInteractor
import com.koenigmed.luomanager.domain.interactor.program.ProgramInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import com.koenigmed.luomanager.util.isPasswordValid
import javax.inject.Inject

@InjectViewState
class AuthPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val authInteractor: AuthInteractor,
        private val programInteractor: ProgramInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<AuthView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        programInteractor.initPrograms()
    }

    fun login(username: String, password: String) {
        if (username.isBlank()) {
            viewState.showMessage(resourceManager.getString(R.string.error_invalid_username))
            return
        }
        if (isPasswordValid(password, resourceManager)) {
            viewState.showMessage(resourceManager.getString(R.string.error_invalid_password))
            return
        }
        authInteractor.login(username, password)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe { viewState.showProgress(true) }
                .doAfterTerminate { viewState.showProgress(false) }
                .subscribe(
                        { router.startFlow(Screens.MAIN_FLOW) },
                        { errorHandler.proceed(it, { viewState.showMessage(it) }) }
                ).connect()
    }

    fun onBackPressed() = router.exit()
}