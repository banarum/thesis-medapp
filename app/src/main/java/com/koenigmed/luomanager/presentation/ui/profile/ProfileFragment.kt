package com.koenigmed.luomanager.presentation.ui.profile

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.extension.visibleOrGone
import com.koenigmed.luomanager.presentation.mvp.profile.ProfileFeelsState
import com.koenigmed.luomanager.presentation.mvp.profile.ProfilePresenter
import com.koenigmed.luomanager.presentation.mvp.profile.ProfileView
import com.koenigmed.luomanager.presentation.mvp.profile.UserProfilePresentation
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.presentation.ui.widget.PainLevelView
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.content_profile_feels.*
import kotlinx.android.synthetic.main.content_profile_feels_thanks.*
import kotlinx.android.synthetic.main.content_profile_pain_level.*
import kotlinx.android.synthetic.main.content_profile_user_data.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import toothpick.Toothpick

class ProfileFragment : BaseFragment(), ProfileView {

    override val layoutRes = R.layout.fragment_profile

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @ProvidePresenter
    fun providePresenter(): ProfilePresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(ProfilePresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(toolbar) {
            navigationIcon = null
            title = getString(R.string.profile_title)
        }

        initUserData()
        initFeelsData()
        initPainLevelData()
    }

    override fun setLoading(isLoading: Boolean, isSuccess: Boolean) {
        toolbar_progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
        toolbar_device_fail.visibility = if (isLoading || isSuccess) View.GONE else View.VISIBLE
        bt_connection.visibility = if (isLoading || !isSuccess) View.GONE else View.VISIBLE
    }

    private fun initUserData() {
        profileEditImageView.setOnClickListener {
            presenter.onEditProfileClick()
        }
        profileFaqImageView.setOnClickListener {
            presenter.showFaq()
        }
    }

    override fun showProfileData(profileData: UserProfilePresentation) {
        val userData = profileData.userData
        profileUserName.text = userData.getUsername()
        profileUserInfo.text = getString(R.string.profile_data_format, userData.getAgeString(), userData.height,
                userData.weight)

        profileFeelsLayout.visibleOrGone(profileData.showFeels)
        profileFeelsThanksLayout.visibleOrGone(false)
        profilePainLevelLayout.visibleOrGone(profileData.showPainLevel)
    }

    override fun showFeelsThanks() {
        profileFeelsLayout.visibleOrGone(false)
        profileFeelsThanksLayout.visibleOrGone(true)
    }

    private fun initFeelsData() {
        profile_feels_no_button.setOnClickListener {
            presenter.onFeelsClick(ProfileFeelsState.NO)
        }
        profile_feels_yes_button.setOnClickListener {
            presenter.onFeelsClick(ProfileFeelsState.YES)
        }
        profile_feels_undefined_button.setOnClickListener {
            presenter.onFeelsClick(ProfileFeelsState.UNDEFINED)
        }
    }

    private fun initPainLevelData() {
        profilePainLevelView.setOnPainLevelClickListener(object : PainLevelView.OnItemClickListener {
            override fun onItemClick(id: Int) {
                presenter.onPainLevelClick(id)
            }
        })
    }

    override fun setBattery(charge: Int) {
        battery_view.visibility = View.VISIBLE
        battery_view.setPercent(charge)
    }

    override fun setBtPower(state: BtInteractor.BtPower) {
        when (state){
            BtInteractor.BtPower.STRONG -> bt_connection.setImageResource(R.drawable.bt_strong)
            BtInteractor.BtPower.MID -> bt_connection.setImageResource(R.drawable.bt_mid)
            BtInteractor.BtPower.WEAK -> bt_connection.setImageResource(R.drawable.bt_weak)
        }
    }

    companion object {
    }
}