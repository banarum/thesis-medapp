package com.koenigmed.luomanager.presentation.ui.main

import android.os.Bundle
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.presentation.mvp.main.MainPresenter
import com.koenigmed.luomanager.presentation.mvp.main.MainView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.presentation.ui.profile.ProfileFragment
import com.koenigmed.luomanager.presentation.ui.settings.SettingsFragment
import com.koenigmed.luomanager.presentation.ui.treatment.TreatmentFragment
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.util.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.fragment_main.*
import toothpick.Toothpick

class MainFragment : BaseFragment(), MainView {
    override val layoutRes = R.layout.fragment_main

    private lateinit var tabs: HashMap<String, BaseFragment>
    private val tabKeys = listOf(
            tabIdToFragmentTag(R.id.tab_profile),
            tabIdToFragmentTag(R.id.tab_treatment),
            tabIdToFragmentTag(R.id.tab_settings)
    )
    private var tabMenuPositionsIdsMap: Map<Int, Int> = mapOf(
            POSITION_PROFILE to R.id.tab_profile,
            POSITION_TREATMENT to R.id.tab_treatment,
            POSITION_SETTINGS to R.id.tab_settings
    )

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter {
        return Toothpick
                .openScope(DI.APP_SCOPE)
                .getInstance(MainPresenter::class.java)
    }

    private var lastTabKeyPositionSelected: Int = 0

    private fun tabIdToFragmentTag(id: Int) = "tab_$id"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(bottomBar) {
            BottomNavigationViewHelper.removeShiftMode(this)
            setOnNavigationItemSelectedListener { menuItem ->
                showTab(menuItem)
                true
            }
        }

        if (savedInstanceState == null && !::tabs.isInitialized) {
            tabs = createNewFragments()
            childFragmentManager.beginTransaction()
                    .add(R.id.main_screen_container, tabs[tabKeys[0]], tabKeys[0])
                    .add(R.id.main_screen_container, tabs[tabKeys[1]], tabKeys[1])
                    .add(R.id.main_screen_container, tabs[tabKeys[2]], tabKeys[2])
                    .hide(tabs[tabKeys[POSITION_PROFILE]])
                    .hide(tabs[tabKeys[POSITION_SETTINGS]])
                    .commitNow()
            bottomBar.selectedItemId = tabMenuPositionsIdsMap[POSITION_TREATMENT]!!
        } else {
            tabs = findFragments()
        }
    }

    private fun showTab(menuItem: MenuItem) {
        val newItem = tabIdToFragmentTag(menuItem.itemId)
        childFragmentManager.beginTransaction()
                .hide(tabs[tabKeys[lastTabKeyPositionSelected]])
                .show(tabs[newItem])
                .commit()
        lastTabKeyPositionSelected = tabKeys.indexOf(newItem)
    }

    private fun createNewFragments(): HashMap<String, BaseFragment> = hashMapOf(
            tabKeys[POSITION_PROFILE] to ProfileFragment(),
            tabKeys[POSITION_TREATMENT] to TreatmentFragment(),
            tabKeys[POSITION_SETTINGS] to SettingsFragment()
    )

    private fun findFragments(): HashMap<String, BaseFragment> = hashMapOf(
            tabKeys[0] to childFragmentManager.findFragmentByTag(tabKeys[0]) as BaseFragment,
            tabKeys[1] to childFragmentManager.findFragmentByTag(tabKeys[1]) as BaseFragment,
            tabKeys[2] to childFragmentManager.findFragmentByTag(tabKeys[2]) as BaseFragment
    )

    override fun onBackPressed() = presenter.onBackPressed()

    companion object {
        const val POSITION_PROFILE = 0
        const val POSITION_TREATMENT = 1
        const val POSITION_SETTINGS = 2
    }
}