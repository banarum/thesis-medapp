package com.koenigmed.luomanager.presentation.mvp.profile.faq

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.domain.interactor.profile.ProfileInteractor
import com.koenigmed.luomanager.presentation.flow.FlowRouter
import com.koenigmed.luomanager.presentation.global.ErrorHandler
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import com.koenigmed.luomanager.system.IResourceManager
import com.koenigmed.luomanager.system.SchedulersProvider
import javax.inject.Inject

@InjectViewState
class FaqPresenter @Inject constructor(
        private val resourceManager: IResourceManager,
        private val router: FlowRouter,
        private val profileInteractor: ProfileInteractor,
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider
) : BasePresenter<FaqView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showFaq("Faq")
    }

    fun onBackPressed() = router.exit()

}