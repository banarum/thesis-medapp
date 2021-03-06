package com.koenigmed.luomanager.presentation.ui.treatment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.interactor.device.BtInteractor
import com.koenigmed.luomanager.extension.getDrawable
import com.koenigmed.luomanager.extension.visibleOrGone
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.presentation.mvp.treatment.MyoProgramHistoryPresentation
import com.koenigmed.luomanager.presentation.mvp.treatment.TreatmentPresenter
import com.koenigmed.luomanager.presentation.mvp.treatment.TreatmentState
import com.koenigmed.luomanager.presentation.mvp.treatment.TreatmentView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.presentation.ui.widget.BatteryView
import com.koenigmed.luomanager.presentation.ui.widget.BottomSpacesItemDecoration
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.content_treatment_no_device_connection.*
import kotlinx.android.synthetic.main.content_treatment_schedule.*
import kotlinx.android.synthetic.main.content_treatment_start.*
import kotlinx.android.synthetic.main.fragment_treatment.*
import kotlinx.android.synthetic.main.toolbar.*
import org.threeten.bp.LocalDate
import timber.log.Timber
import toothpick.Toothpick
import javax.inject.Inject

class TreatmentFragment : BaseFragment(), TreatmentView {

    override val layoutRes = R.layout.fragment_treatment

    @InjectPresenter
    lateinit var presenter: TreatmentPresenter

    @ProvidePresenter
    fun providePresenter(): TreatmentPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(TreatmentPresenter::class.java)
    }

    private val scheduleAdapter: TreatmentScheduleAdapter by lazy {
        TreatmentScheduleAdapter()
    }
    private val historyAdapter: TreatmentHistoryAdapter by lazy {
        TreatmentHistoryAdapter(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.apply {
            navigationIcon = null
            title = getString(R.string.treatment_title)
            inflateMenu(R.menu.menu_treatment)
        }

        treatmentStartImageView.setOnClickListener {
            presenter.onStartClick()
        }
        treatmentStartTextView.setOnClickListener {
            presenter.onStartClick()
        }
        treatmentProgramTitleTextView.setOnClickListener {
            presenter.onProgramClick()
        }
        treatmentProgramNameTextView.setOnClickListener {
            presenter.onProgramClick()
        }

        treatment_btn_sync.setOnClickListener {
            presenter.onSyncClick()
        }

        treatmentScheduleRecycler.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(BottomSpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.treatment_schedule_grid_bottom_space)))
            adapter = this@TreatmentFragment.scheduleAdapter
        }
    }

    override fun showProgram(myoProgram: MyoProgramPresentation) {
        treatmentProgramNameTextView.text = myoProgram.name
    }

    override fun setScreenState(state: TreatmentState) {
        when (state) {
            is TreatmentState.Manual -> {
                showManualState(state)
            }
            is TreatmentState.Schedule -> {
                showScheduleState(state)
            }
            is TreatmentState.NoDeviceConnection -> {
                showNoDeviceConnection()
            }
        }
    }

    private fun showManualState(state: TreatmentState.Manual) {
        setScheduleLayoutVisible(false)
        setNoDeviceConnectionLayoutVisible(false)
        when (state) {
            is TreatmentState.Manual.Idle -> {
                setStopLayoutVisible(false)
                setStartLayoutVisible(true)
            }
            is TreatmentState.Manual.Running -> {
                setStartLayoutVisible(false)
                setStopLayoutVisible(true, state.getTimeRunningString())
            }
        }
    }

    private fun showScheduleState(state: TreatmentState.Schedule) {
        setStartLayoutVisible(false)
        setStopLayoutVisible(false)
        setNoDeviceConnectionLayoutVisible(false)
        setScheduleLayoutVisible(true)
        treatmentScheduleRecycler.post {
            scheduleAdapter.setData(state.schedule)
        }
    }

    private fun showNoDeviceConnection() {
        setStartLayoutVisible(false)
        setStopLayoutVisible(false)
        setScheduleLayoutVisible(false)
        setNoDeviceConnectionLayoutVisible(true)
    }

    private fun setStartLayoutVisible(visible: Boolean) {
        treatmentStartTextView.visibleOrGone(visible)
        treatmentStartImageView.visibleOrGone(visible)
        treatmentStartTextView.text = getString(R.string.treatment_start)
        treatmentStartImageView.background = getDrawable(R.drawable.ic_treatment_start)
    }

    private fun setStopLayoutVisible(visible: Boolean, time: String? = null) {
        treatmentStartTextView.visibleOrGone(visible)
        treatmentStartImageView.visibleOrGone(visible)
        treatmentStartTextView.text = getString(R.string.treatment_stop)
        treatmentStartImageView.background = getDrawable(R.drawable.ic_treatment_stop_with_bg)
        treatmentStopTimeTextView.visibleOrGone(visible)
        time?.let {
            showTimeRunning(time)
        }
    }

    private fun setScheduleLayoutVisible(visible: Boolean) {
        treatmentScheduleTitleTextView.visibleOrGone(visible)
        treatmentScheduleRecycler.visibleOrGone(visible)
    }

    override fun showTimeRunning(time: String) {
        treatmentStopTimeTextView.visibleOrGone(true)
        treatmentStopTimeTextView.text = time
    }

    private fun setNoDeviceConnectionLayoutVisible(visible: Boolean) {
        treatmentNoDeviceConnectionImage.visibleOrGone(visible)
        treatmentNoDeviceConnectionTitle.visibleOrGone(visible)
        treatmentConnectButton.visibleOrGone(visible)
    }

    override fun setLoading(isLoading: Boolean, isSuccess: Boolean) =
            TreatmentFragment.setLoading(toolbar_progress_bar, toolbar_device_fail, bt_connection, battery_view, isLoading, isSuccess)

    override fun setBattery(charge: Int) =
            TreatmentFragment.setBattery(battery_view, charge)

    override fun setBtPower(state: BtInteractor.BtPower) =
            TreatmentFragment.setBtPower(bt_connection, state)

    companion object {
        fun setLoading(toolbar_progress_bar: View, toolbar_device_fail: View, bt_connection: View, battery_view: BatteryView, isLoading: Boolean, isSuccess: Boolean) {
            toolbar_progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
            toolbar_device_fail.visibility = if (isLoading || isSuccess) View.GONE else View.VISIBLE
            bt_connection.visibility = if (isLoading || !isSuccess) View.GONE else View.VISIBLE
            battery_view.visibility = View.VISIBLE

            if (isLoading || !isSuccess) {
                battery_view.setPercent(0)
            }
        }

        fun setBattery(battery_view: BatteryView, charge: Int) {
            battery_view.visibility = View.VISIBLE
            battery_view.setPercent(charge)
        }

        fun setBtPower(bt_connection: ImageView, state: BtInteractor.BtPower) {
            when (state){
                BtInteractor.BtPower.STRONG -> bt_connection.setImageResource(R.drawable.bt_strong)
                BtInteractor.BtPower.MID -> bt_connection.setImageResource(R.drawable.bt_mid)
                BtInteractor.BtPower.WEAK -> bt_connection.setImageResource(R.drawable.bt_weak)
                BtInteractor.BtPower.EXTRA_WEAK -> bt_connection.setImageResource(R.drawable.bt_extra_weak)
            }
        }
    }
}