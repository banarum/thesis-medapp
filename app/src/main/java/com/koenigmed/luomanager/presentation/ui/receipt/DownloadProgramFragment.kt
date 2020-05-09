package com.koenigmed.luomanager.presentation.ui.receipt

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.program.DownloadProgramPresenter
import com.koenigmed.luomanager.presentation.mvp.program.DownloadProgramView
import com.koenigmed.luomanager.presentation.mvp.program.DownloadProgramsPresentation
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.util.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_download_program.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class DownloadProgramFragment : BaseFragment(), DownloadProgramView {

    override val layoutRes: Int
        get() = R.layout.fragment_download_program

    @InjectPresenter
    lateinit var presenter: DownloadProgramPresenter

    @ProvidePresenter
    fun providePresenter(): DownloadProgramPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(DownloadProgramPresenter::class.java)
    }

    private val programsAdapter: DownloadProgramsAdapter by lazy {
        DownloadProgramsAdapter { programId ->
            presenter.downloadProgram(programId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.download_receipt_title)
            setNavigationOnClickListener { onBackPressed() }
        }
        programsDownloadRecycler.apply {
            setEmptyView(programsDownloadEmpty)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = this@DownloadProgramFragment.programsAdapter
        }

        initSearchView()
    }

    private fun initSearchView() {
        programDownloadSearchView.onActionViewExpanded()
        hideKeyboard(activity)
        programDownloadSearchView.clearFocus()
        RxSearchView.queryTextChanges(programDownloadSearchView)
                .skipInitialValue()
                .observeOn(Schedulers.computation())
                .map { it.toString() }
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .switchMap {
                    presenter.getPrograms(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Timber.d("queryTextChanges result " + it)
                            showPrograms(it)
                        },
                        { Timber.e("queryTextChanges: $it") }
                )
                .connect()
    }

    override fun showPrograms(programs: DownloadProgramsPresentation) {
        programsAdapter.setData(programs.programs)
        programsDownloadRecycler.changeVisibility()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }

}