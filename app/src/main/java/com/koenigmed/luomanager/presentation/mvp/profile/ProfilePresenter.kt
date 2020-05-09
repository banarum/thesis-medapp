package com.koenigmed.luomanager.presentation.mvp.profile

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.domain.interactor.profile.ProfileInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.flow.Screens.RESULT_CODE_PROFILE_EDIT
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val profileInteractor: ProfileInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<ProfileView>() {

    init {
        router.setResultListener(RESULT_CODE_PROFILE_EDIT) { _ ->
            getUserInfo()
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUserInfo()
    }

    fun getUserInfo() {
        profileInteractor.getUserProfileData()
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({ viewState.showProfileData(it) }, { Timber.e(it) })
    }

    fun onBackPressed() = router.exit()

    override fun onDestroy() {
        router.removeResultListener(RESULT_CODE_PROFILE_EDIT)
    }

    fun onEditProfileClick() {
        router.navigateTo(Screens.PROFILE_EDIT_SCREEN)
    }

    fun onFeelsClick(state: ProfileFeelsState) {
        profileInteractor.onFeelsChosen(state)
        viewState.showFeelsThanks()
    }

    fun onPainLevelClick(painLevel: Int) {
        Timber.d("painLevel " + painLevel)
        profileInteractor.onPainLevelChosen()
    }

    fun showFaq() {
        router.navigateTo(Screens.FAQ)
    }
}