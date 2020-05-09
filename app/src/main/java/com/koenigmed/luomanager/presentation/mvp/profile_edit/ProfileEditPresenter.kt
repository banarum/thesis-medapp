package com.koenigmed.luomanager.presentation.mvp.profile_edit

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.profile.ProfileInteractor
import com.koenigmed.luomanager.extension.removeMeasure
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.flow.Screens.RESULT_CODE_PROFILE_EDIT
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProfileEditPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val profileInteractor: ProfileInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<ProfileEditView>() {

    private lateinit var userData: UserDataPresentation

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        profileInteractor.getUserData()
                .doOnSuccess { this.userData = it }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({ viewState.showProfileData(it) }, { Timber.e(it) })
    }

    fun onSaveClick() {
        if (!userData.isReady()) {
            viewState.showMessage(resourceManager.getString(R.string.profile_validate_error))
            return
        }
        profileInteractor.saveUserData(userData)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe { viewState.showProgress(true) }
                .doAfterTerminate { viewState.showProgress(false) }
                .subscribe(
                        {
                            router.exitWithResult(RESULT_CODE_PROFILE_EDIT, null)
                        }
                ) { errorHandler.proceed(it) { viewState.showMessage(it) } }.connect()
    }

    fun onBackPressed() = router.exit()

    fun onSurnameClick() {
        viewState.showSurnameDialog(userData.surname)
    }

    fun onNameClick() {
        viewState.showNameDialog(userData.name)
    }

    fun onSurnameEntered(surname: String) {
        if (surname.isNotBlank()) {
            viewState.showSurname(surname)
            userData.surname = surname
            checkReady()
        }
    }

    fun onNameEntered(name: String) {
        if (name.isNotBlank()) {
            viewState.showName(name)
            userData.name = name
            checkReady()
        }
    }

    fun onAgeSet(birthDate: LocalDate) {
        userData.birthDate = birthDate
        viewState.showAge(userData.getAgeString())
        checkReady()
    }

    fun onHeightSet(height: String) {
        userData.height = height
        viewState.showHeight(height)
        checkReady()
    }

    fun onWeightSet(weight: String) {
        userData.weight = weight
        viewState.showWeight(weight)
        checkReady()
    }

    private fun checkReady() {
    }

    fun onAgeClick() {
        viewState.showAgeDatePicker(userData.birthDate ?: LocalDate.now().minusYears(18))
    }

    fun onWeightClick() {
        viewState.showWeightPicker(userData.weight.removeMeasure().toInt())
    }

    fun onHeightClick() {
        viewState.showHeightPicker(userData.height.removeMeasure().toInt())
    }

}