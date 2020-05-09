package com.koenigmed.luomanager.presentation.mvp.main

import com.arellomobile.mvp.InjectViewState
import com.koenigmed.luomanager.presentation.mvp.base.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
        private val router: Router
) : BasePresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun onBackPressed() = router.exit()
}
