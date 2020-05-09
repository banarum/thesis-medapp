package com.koenigmed.luomanager.presentation.ui.profile

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.profile.faq.FaqPresenter
import com.koenigmed.luomanager.presentation.mvp.profile.faq.FaqView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.toolbar.*
import toothpick.Toothpick

class FaqFragment : BaseFragment(), FaqView {
    override val layoutRes = R.layout.fragment_faq

    @InjectPresenter
    lateinit var presenter: FaqPresenter

    @ProvidePresenter
    fun providePresenter(): FaqPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(FaqPresenter::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.faq)
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    override fun showFaq(faq: String) {

    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

}