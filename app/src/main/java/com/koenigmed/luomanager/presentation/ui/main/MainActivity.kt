package com.koenigmed.luomanager.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.flow.FlowNavigator
import com.koenigmed.luomanager.presentation.flow.Screens
import com.koenigmed.luomanager.presentation.mvp.main.MainActivityPresenter
import com.koenigmed.luomanager.presentation.mvp.main.MainActivityView
import com.koenigmed.luomanager.presentation.ui.device_search.DeviceSearchFragment
import com.koenigmed.luomanager.presentation.ui.global.BaseActivity
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.presentation.ui.profile.FaqFragment
import com.koenigmed.luomanager.presentation.ui.profile.ProfileEditFragment
import com.koenigmed.luomanager.presentation.ui.program.ProgramFragment
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment
import com.koenigmed.luomanager.presentation.ui.receipt.DownloadProgramFragment
import com.koenigmed.luomanager.presentation.ui.receipt.ViewProgramBundle
import com.koenigmed.luomanager.presentation.ui.receipt.ViewProgramFragment
import com.koenigmed.luomanager.presentation.ui.sync.SyncFragment
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.toothpick.module.MainActivityModule
import ru.terrakok.cicerone.commands.Command
import toothpick.Toothpick

class MainActivity : BaseActivity(), MainActivityView {

    override val layoutRes = R.layout.activity_container

    private val currentFragment
        get() = supportFragmentManager.findFragmentById(R.id.container) as BaseFragment?

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter(): MainActivityPresenter {
        return Toothpick
                .openScope(DI.SERVER_SCOPE)
                .getInstance(MainActivityPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        Toothpick.openScopes(DI.SERVER_SCOPE, DI.MAIN_ACTIVITY_SCOPE).apply {
            installModules(MainActivityModule())
            Toothpick.inject(this@MainActivity, this)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) Toothpick.closeScope(DI.MAIN_ACTIVITY_SCOPE)
    }

    override fun initMainScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment())
                .commitNow()
    }

    override val navigator = object : FlowNavigator(this, R.id.container) {

        override fun applyCommands(commands: Array<out Command>?) {
            super.applyCommands(commands)
        }

        override fun createFlowIntent(flowKey: String, data: Any?) =
                Screens.getFlowIntent(this@MainActivity, flowKey, data)

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.MAIN_SCREEN -> MainFragment()
            Screens.PROFILE_EDIT_SCREEN -> ProfileEditFragment()
            Screens.SYNC_SCREEN -> if (data != null) {
                // programId
                SyncFragment.newInstance(data.toString())
            } else {
                SyncFragment()
            }
            Screens.DEVICE_SEARCH_SCREEN -> DeviceSearchFragment()
            Screens.PROGRAM_SCREEN -> ProgramFragment()
            Screens.PROGRAM_CREATE_RECEIPT_SCREEN -> if (data != null) {
                CreateProgramFragment.newInstance(data.toString())
            } else {
                CreateProgramFragment()
            }
            Screens.PROGRAM_VIEW_RECEIPT_SCREEN -> if (data != null) {
                ViewProgramFragment.newInstance(data as ViewProgramBundle)
            }else {
                ViewProgramFragment()
            }
            Screens.PROGRAM_DOWNLOAD_RECEIPT_SCREEN -> DownloadProgramFragment()
            Screens.FAQ -> FaqFragment()
            else -> null
        }
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: presenter.onBackPressed()
    }

    companion object {
        fun getStartIntent(context: Context) =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
    }
}
