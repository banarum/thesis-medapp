package com.koenigmed.luomanager.presentation.ui.program

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.program.ProgramPresenter
import com.koenigmed.luomanager.presentation.mvp.program.ProgramView
import com.koenigmed.luomanager.presentation.mvp.program.ProgramsPresentation
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.util.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_program.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class ProgramFragment : BaseFragment(), ProgramView {

    override val layoutRes: Int
        get() = R.layout.fragment_program

    @InjectPresenter
    lateinit var presenter: ProgramPresenter

    @ProvidePresenter
    fun providePresenter(): ProgramPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(ProgramPresenter::class.java)
    }

    private var actionMode: ActionMode? = null

    private val programsAdapter: ProgramsAdapter by lazy {
        ProgramsAdapter({ programId ->
            selectProgram(false, programId)
            presenter.onSelectedProgramSwitch(programId)
        }, {})
    }

    override fun selectProgram(userProgmar: Boolean, programId: String) {
        if (userProgmar) {
            userProgramsAdapter.switchItem(programId)
        } else {
            programsAdapter.switchItem(programId)
        }
    }

    private val userProgramsAdapter: ProgramsAdapter by lazy {
        ProgramsAdapter({ programId ->
            selectProgram(true, programId)
            presenter.onSelectedProgramSwitch(programId)
        }, { programId ->
            presenter.onProgramAddedToDelete(programId)
            userProgramsAdapter.deleteItem(programId)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.program_title)
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.menu_program)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_program_edit -> {
                        presenter.onProgramEditClick()
                    }
                }
                true
            }
        }

        programNewReceiptButton.setOnClickListener {
            presenter.onNewReceiptClick()
        }

        programsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = this@ProgramFragment.programsAdapter
        }
        userProgramsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = this@ProgramFragment.userProgramsAdapter
        }
        initSearchView()
    }

    override fun showProgramsEdit() {
        actionMode = (activity as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
        actionMode?.title = getString(R.string.program_title)
    }

    private val actionModeCallbacks = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            menu.add(getString(R.string.ready))
            presenter.onMultiSelectEnabled(true)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            presenter.onDeleteProgramsClick()
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            Timber.d("onDestroyActionMode")
            presenter.onMultiSelectEnabled(false)
        }
    }

    override fun showMultiSelectEnabled(multiSelect: Boolean) {
        if (!multiSelect) {
            actionMode?.finish()
        }
        programNewReceiptButton.isEnabled = !multiSelect
        programsAdapter.onMultiSelectEnabled(multiSelect)
        userProgramsAdapter.onMultiSelectEnabled(multiSelect)
    }

    private fun initSearchView() {
        programSearchView.onActionViewExpanded()
        hideKeyboard(activity)
        programSearchView.clearFocus()
        RxSearchView.queryTextChanges(programSearchView)
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

    override fun showPrograms(programs: ProgramsPresentation) {
        programsRecycler.post {
            programsAdapter.setData(programs.programs)
        }
        userProgramsRecycler.post {
            userProgramsAdapter.setData(programs.userPrograms)
        }
    }

    override fun showReceiptChooseDialog() {
        val adb = AlertDialog.Builder(activity!!)
        val items = arrayOf<CharSequence>(getString(R.string.program_dialog_new_receipt),
                getString(R.string.program_dialog_download))
        adb.setItems(items) { dialog, which ->
            dialog.dismiss()
            when (which) {
                0 -> presenter.onCreateReceiptChoose()
                1 -> presenter.onDownloadReceiptChoose()
            }
        }
        adb.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }
}