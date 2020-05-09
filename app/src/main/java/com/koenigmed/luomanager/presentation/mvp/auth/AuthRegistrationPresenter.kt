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
import com.koenigmed.luomanager.util.isPasswordValid
import com.koenigmed.luomanager.util.isValidEmail
import org.threeten.bp.LocalDate
import javax.inject.Inject

@InjectViewState
class AuthRegistrationPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val authInteractor: AuthInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<AuthRegistrationView>() {

    private val registerPresentation: RegisterPresentation = RegisterPresentation()
    private var heightPickerPosition = -1
    private var weightPickerPosition = -1
    private var genderPickerPosition = -1

    fun register() {
        if (!isValidEmail(registerPresentation.email)) {
            viewState.showMessage(resourceManager.getString(R.string.error_invalid_email))
            return
        }
        if (isPasswordValid(registerPresentation.password, resourceManager)) {
            viewState.showMessage(resourceManager.getString(R.string.error_invalid_password))
            return
        }
        if (!registerPresentation.isReady()) {
            viewState.showMessage(resourceManager.getString(R.string.create_receipt_error_validate))
            return
        }
        authInteractor.register(registerPresentation)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .doOnSubscribe { viewState.showProgress(true) }
                .doAfterTerminate { viewState.showProgress(false) }
                .subscribe(
                        {
                            router.startFlow(Screens.MAIN_FLOW)
                        },
                        { errorHandler.proceed(it, { viewState.showMessage(it) }) }
                ).connect()
    }

    fun onBackPressed() = router.exit()

    fun onEmailEntered(email: String) {
        registerPresentation.email = email
        checkReady()
    }

    fun onPasswordEntered(password: String) {
        registerPresentation.password = password
        checkReady()
    }

    fun onNameEntered(name: String) {
        registerPresentation.name = name
        checkReady()
    }

    fun onSurnameEntered(surname: String) {
        registerPresentation.surname = surname
        checkReady()
    }

    fun onGenderSet(gender: String, position: Int) {
        genderPickerPosition = position
        registerPresentation.gender = gender
        viewState.showGender(gender)
        checkReady()
    }

    fun onBirthDateSet(birthDate: LocalDate) {
        registerPresentation.birthDate = birthDate
        viewState.showAge(registerPresentation.getAge())
        checkReady()
    }

    fun onHeightSet(height: String, position: Int) {
        heightPickerPosition = position
        registerPresentation.height = height
        viewState.showHeight(height)
        checkReady()
    }

    fun onWeightSet(weight: String, position: Int) {
        weightPickerPosition = position
        registerPresentation.weight = weight
        viewState.showWeight(weight)
        checkReady()
    }

    private fun checkReady() {
        viewState.setRegisterButtonEnabled(registerPresentation.isReady())
    }

    fun onAgeClick() {
        viewState.showAgeDatePicker(registerPresentation.birthDate ?: LocalDate.now().minusYears(18))
    }

    fun onWeightClick() {
        viewState.showWeightPicker(weightPickerPosition)
    }

    fun onHeightClick() {
        viewState.showHeightPicker(heightPickerPosition)
    }

    fun onGenderClick() {
        viewState.showGenderPicker(genderPickerPosition)
    }
}