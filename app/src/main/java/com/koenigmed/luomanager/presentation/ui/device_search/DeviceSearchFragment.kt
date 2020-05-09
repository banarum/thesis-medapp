package com.koenigmed.luomanager.presentation.ui.device_search

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.RxView
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getColor
import com.koenigmed.luomanager.extension.getDimension
import com.koenigmed.luomanager.extension.showBtSettings
import com.koenigmed.luomanager.extension.visibleOrGone
import com.koenigmed.luomanager.presentation.mvp.device_search.DeviceSearchPresenter
import com.koenigmed.luomanager.presentation.mvp.device_search.DeviceSearchState
import com.koenigmed.luomanager.presentation.mvp.device_search.DeviceSearchView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import com.koenigmed.luomanager.util.PermissionUtil
import com.koenigmed.luomanager.util.SequenceUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.content_device_search_few_devices.*
import kotlinx.android.synthetic.main.content_device_search_in_progress.*
import kotlinx.android.synthetic.main.content_device_search_no_bt.*
import kotlinx.android.synthetic.main.content_device_search_success.*
import kotlinx.android.synthetic.main.fragment_device_search.*
import kotlinx.android.synthetic.main.toolbar.*
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class DeviceSearchFragment : BaseFragment(), DeviceSearchView {
    override val layoutRes = R.layout.fragment_device_search

    @InjectPresenter
    lateinit var presenter: DeviceSearchPresenter

    @ProvidePresenter
    fun providePresenter(): DeviceSearchPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(DeviceSearchPresenter::class.java)
    }

    private val devicesAdapter: DevicesAdapter by lazy {
        DevicesAdapter { device ->
            presenter.onDeviceChosen(device)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
        if (!context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showMessage(getString(R.string.error_device_does_not_support_bluetooth))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.back_title)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }

        RxView.clicks(device_search_no_bt_open_settings_text_view)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    showBtSettings()
                }
                .connect()

        RxView.clicks(deviceSearchButton)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    presenter.onNextButtonClick()
                }
                .connect()

        RxView.clicks(deviceSearchSuccessAnotherDeviceTextView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    presenter.onHaveAnotherDeviceClick()
                }
                .connect()

        deviceSearchInProgressImageView.setOnClickListener {
            presenter.onSearchImageClick()
        }
        deviceSearchDevicesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = devicesAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        deviceSearchFewDevicesAnotherDeviceTextView.setOnClickListener {
            presenter.onHaveAnotherDeviceClick()
        }
    }

    override fun askForPermissionIfNeed() {
        if (!PermissionUtil.hasPermissions(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSIONS_REQUEST_LOCATION)
        } else {
            presenter.onLocationPermissionGranted(true)
        }
    }

    override fun showDataDialog() {
        AlertDialog.Builder(activity!!, R.style.AlertDialogStyle)
                .setTitle(getString(R.string.device_search_data_dialog_title))
                .setMessage(getString(R.string.device_search_data_dialog_message))
                .setPositiveButton(R.string.device_search_data_dialog_accept) { _, _ -> }
                .setNegativeButton(R.string.device_search_data_dialog_decline) { dialog, _ ->
                    dialog.dismiss()
                    activity!!.finish()
                }
                .setOnDismissListener {
                    presenter.onDataDialogDismissed()
                }
                .create()
                .apply {
                    show()
                    setCancelable(false)
                    getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.settings_dialog_cancel_button_text_color))
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.settings_dialog_ok_button_text_color))
                }
    }

    override fun setScreenState(state: DeviceSearchState) {
        when (state) {
            is DeviceSearchState.NoBluetooth -> {
                setSuccessLayoutVisible(false)
                setNoBtLayoutVisible(true)
                setFewDevicesLayoutVisible(false)
            }
            is DeviceSearchState.Running -> {
                setNoBtLayoutVisible(false)
                setSuccessLayoutVisible(false)
                setSearchLayoutVisible(true)
                setFewDevicesLayoutVisible(false)
            }
            is DeviceSearchState.FewDevices -> {
                setNoBtLayoutVisible(false)
                setSuccessLayoutVisible(false)
                setSearchLayoutVisible(false)
                setFewDevicesLayoutVisible(true, state.devices)
            }
            is DeviceSearchState.Connected -> {
                setNoBtLayoutVisible(false)
                setSearchLayoutVisible(false)
                setFewDevicesLayoutVisible(false)
                setSuccessLayoutVisible(true, state.deviceName)
            }
        }
    }

    private fun setNoBtLayoutVisible(visible: Boolean) {
        deviceSearchNoBtLayout.visibleOrGone(visible)
        deviceSearchButton.visibleOrGone(true)
        setNextButtonEnabled(false)
        deviceSearchButton.text = getString(R.string.device_search_next)
    }

    private fun setSearchLayoutVisible(visible: Boolean) {
        deviceSearchInProgressLayout.visibleOrGone(visible)
        setNextButtonEnabled(false)
        deviceSearchButton.text = getString(R.string.device_search_next)
    }

    private fun setFewDevicesLayoutVisible(visible: Boolean, devices: List<BluetoothDevice>? = null) {
        deviceSearchFewDevicesLayout.visibleOrGone(visible)
        deviceSearchButton.visibleOrGone(false)
        if (devices != null) {
            deviceSearchDevicesRecycler.post {
                devicesAdapter.setData(devices)
            }
        }
    }

    private fun setSuccessLayoutVisible(visible: Boolean, deviceName: String? = null) {
        deviceSearchSuccessLayout.visibleOrGone(visible)
        deviceSearchButton.visibleOrGone(true)
        setNextButtonEnabled(true)
        deviceSearchButton.text = getString(R.string.device_search_done)
        deviceName?.let {
            deviceSearchSuccessText.text = getString(R.string.device_search_success_text_format, deviceName)
        }
    }

    fun setNextButtonEnabled(enabled: Boolean) {
        deviceSearchButton.isEnabled = enabled
        deviceSearchButton.alpha = if(enabled) 1f else context!!.getDimension(R.dimen.device_search_next_button_alpha)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                presenter.onLocationPermissionGranted(grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        private val PERMISSIONS_REQUEST_LOCATION = SequenceUtil.nextInt
    }
}