package com.koenigmed.luomanager.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getColor
import com.koenigmed.luomanager.presentation.mvp.settings.SettingsPresenter
import com.koenigmed.luomanager.presentation.mvp.settings.SettingsView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.toolbar.*
import toothpick.Toothpick



class SettingsFragment : BaseFragment(), SettingsView {

    override val layoutRes = R.layout.fragment_settings

    @InjectPresenter
    lateinit var presenter: SettingsPresenter

    @ProvidePresenter
    fun providePresenter(): SettingsPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(SettingsPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.apply {
            navigationIcon = null
            title = getString(R.string.settings_title)
        }

        settingsDeviceIcon.setOnClickListener {
            presenter.onDeviceIconClick()
        }

        settingsPushesSwitch.setOnCheckedChangeListener { _, checked ->
            presenter.onPushesSwitchCheckChange(checked)
        }

        //val locale = resources.configuration.locale
        //settings_locale_subtitle.text = locale.displayName
        settingsLocaleSubtitle.text = getString(R.string.locale)

        settingsLogoutTitle.setOnClickListener {
            presenter.onLogoutClick()
        }

        settingsDevsLetterTitle.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "abc@gmail.com", null))//todo devs letter data
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
            startActivity(Intent.createChooser(emailIntent, getString(R.string.settings_devs_letter_title)))
        }
    }

    override fun showUnbindDeviceDialog(deviceName: String) {
        val dialog = AlertDialog.Builder(activity!!, R.style.AlertDialogStyle)
                .setTitle(getString(R.string.settings_unbind_device_dialog_title))
                .setMessage(getString(R.string.settings_unbind_device_dialog_message_format, deviceName))
                .setPositiveButton(R.string.settings_unbind_device_dialog_ok) { _, _ -> presenter.unbindDevice() }
                .setNegativeButton(R.string.settings_unbind_device_dialog_cancel) { dialog, _ -> dialog.dismiss() }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.settings_dialog_cancel_button_text_color))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.settings_dialog_ok_button_text_color))
    }

    override fun showLogoutDialog() {
        val dialog = AlertDialog.Builder(activity!!, R.style.AlertDialogStyle)
                .setTitle(getString(R.string.settings_logout_dialog_title))
                .setMessage(getString(R.string.settings_logout_dialog_message))
                .setPositiveButton(R.string.settings_logout_dialog_ok) { _, _ -> presenter.logout() }
                .setNegativeButton(R.string.settings_logout_dialog_cancel) { dialog, _ -> dialog.dismiss() }
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.settings_dialog_cancel_button_text_color))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.settings_dialog_ok_button_text_color))
    }

    override fun initViews(deviceName: String, pushesEnabled: Boolean) {
        settingsDeviceSubtitle.text = deviceName
        settingsPushesSwitch.post {
            settingsPushesSwitch?.isChecked = pushesEnabled
        }
    }

    companion object {
    }
}