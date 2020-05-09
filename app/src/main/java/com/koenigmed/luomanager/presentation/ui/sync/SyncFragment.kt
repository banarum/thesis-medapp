package com.koenigmed.luomanager.presentation.ui.sync

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.sync.SyncPresenter
import com.koenigmed.luomanager.presentation.mvp.sync.SyncView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.fragment_sync.*
import kotlinx.android.synthetic.main.toolbar.*
import toothpick.Toothpick

class SyncFragment : BaseFragment(), SyncView {
    override val layoutRes = R.layout.fragment_sync

    @InjectPresenter
    lateinit var presenter: SyncPresenter

    private var programId: String? = null

    @ProvidePresenter
    fun providePresenter(): SyncPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(SyncPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
        programId = arguments?.getString(KEY_PROGRAM_ID)
        presenter.programId = programId
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.sync_title)
        }
    }

    override fun showProgress(percent: Int) {
        syncProgressView?.post {
            syncProgressView?.showProgress(percent)
        }
    }

    override fun showSyncFinished() {
        syncProgressView.post {
            syncProgressView.showFinished()
        }
    }

    override fun showSyncFailed(message: String) {
        syncProgressView.post {
            syncProgressView.showFailure(message) {
                presenter.onRetryClick()
            }
        }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        private const val KEY_PROGRAM_ID = "KEY_PROGRAM_ID"

        fun newInstance(programId: String): SyncFragment? {
            val syncFragment = SyncFragment()
            val bundle = Bundle()
            bundle.putString(KEY_PROGRAM_ID, programId)
            syncFragment.arguments = bundle
            return syncFragment
        }
    }
}