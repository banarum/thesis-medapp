package com.koenigmed.luomanager.presentation.ui.receipt

import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.domain.model.program.PulseForm
import com.koenigmed.luomanager.extension.onProgressChanged
import com.koenigmed.luomanager.extension.visibleOrGone
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.presentation.mvp.receipt.*
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.AMPERAGE_MAX
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.AMPERAGE_MIN
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.DURABILITY_MAX
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.DURABILITY_MIN
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.EXECUTION_TIME_MAX
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.EXECUTION_TIME_MIN
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.PULSE_FREQUENCY_MAX
import com.koenigmed.luomanager.presentation.ui.receipt.CreateProgramFragment.Companion.PULSE_FREQUENCY_MIN
import com.koenigmed.luomanager.presentation.ui.sync.SyncFragment
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.content_create_receipt_data.view.*
import kotlinx.android.synthetic.main.fragment_view_program.*
import kotlinx.android.synthetic.main.toolbar.*
import org.threeten.bp.LocalTime
import timber.log.Timber
import toothpick.Toothpick

class ViewProgramFragment : BaseFragment(), ViewReceiptView {
    override val layoutRes: Int
        get() = R.layout.fragment_view_program

    @InjectPresenter
    lateinit var presenter: ViewProgramPresenter

    private lateinit var channel1: View
    private lateinit var channel2: View

    private lateinit var scheduleViews: List<View>

    @ProvidePresenter
    fun providePresenter(): ViewProgramPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(ViewProgramPresenter::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toothpick.inject(this, Toothpick.openScope(DI.MAIN_ACTIVITY_SCOPE))
        super.onCreate(savedInstanceState)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initToolbar()
        initData()
        initChannels()
    }

    private fun initToolbar() {
        toolbar.apply {
            setNavigationOnClickListener { presenter.onBackPressed() }
            inflateMenu(R.menu.menu_view_receipt)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_view_receipt_delete -> {
                        presenter.deleteProgram()
                    }
                    R.id.action_view_receipt_save -> {
                        onSaveClick()
                    }
                }
                true
            }
        }
    }

    private fun initData() {
        if (this.arguments != null){
            this.presenter.setProgramPresentation(this.arguments!!.getString(ViewProgramFragment.KEY_PROGRAM_ID,""))
            if (!this.arguments!!.getBoolean(ViewProgramFragment.KEY_PROGRAM_USER_CREATED)) {
                toolbar.menu.removeItem(R.id.action_view_receipt_delete)
                toolbar.menu.removeItem(R.id.action_view_receipt_save)
            }
        }

        start_btn.setOnClickListener {
            presenter.onStartProgram()
        }

        scheduleViews = listOf(start, startValue, end, endValue)
        type.setOnClickListener { presenter.onProgramTypeClick() }
        typeValue.setOnClickListener { presenter.onProgramTypeClick() }
        executionTimeSeekBar.max = EXECUTION_TIME_MAX - EXECUTION_TIME_MIN
        executionTimeSeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
            executionTimeTextView.text =
                    getString(R.string.create_receipt_execution_time, (EXECUTION_TIME_MIN + progress).toString())
        }
        val executionTime = (EXECUTION_TIME_MAX - EXECUTION_TIME_MIN) / 2
        executionTimeSeekBar.progress = executionTime
        executionTimeTextView.text =
                getString(R.string.create_receipt_execution_time, (executionTime + EXECUTION_TIME_MIN).toString())

        start.setOnClickListener {presenter.onStartClick()}
        startValue.setOnClickListener {presenter.onStartClick() }
        end.setOnClickListener { presenter.onEndClick() }
        endValue.setOnClickListener { presenter.onEndClick() }
    }

    override fun setProgramChannel(id: Int, activated: Boolean, impulseType: String, bipolar: Boolean, powerA: Int, duration: Int, frequency: Int) {
        if (id==1) {
            channel1.visibility = View.VISIBLE
            channel1.channelStatusSwitch.isChecked = activated
            channel1.pulseFormValueTextView.text = impulseType
            channel1.bipolarRegimeSwitch.isChecked = bipolar
            channel1.amperageSeekBar.progress = powerA
            channel1.pulseDurabilitySeekBar.progress = duration - DURABILITY_MIN
            channel1.pulseFrequencySeekBar.progress = frequency - PULSE_FREQUENCY_MIN
        } else {
            channel2.visibility = View.VISIBLE
            channel2.channelStatusSwitch.isChecked = activated
            channel2.pulseFormValueTextView.text = impulseType
            channel2.bipolarRegimeSwitch.isChecked = bipolar
            channel2.amperageSeekBar.progress = powerA
            channel2.pulseDurabilitySeekBar.progress = duration - DURABILITY_MIN
            channel2.pulseFrequencySeekBar.progress = frequency - PULSE_FREQUENCY_MIN
        }
    }

    override fun setProgramStartTime(time: String) {
        startValue.text = time
    }

    override fun setProgramEndTime(time: String) {
    endValue.text = time
    }

    override fun setProgramTitle(title: String) {
        toolbar.title = title
    }

    override fun setProgramType(typeName: String) {
        typeValue.text = typeName
    }

    private fun initChannels() {
        channel1 = layoutInflater.inflate(R.layout.content_create_receipt_data, channelContainer, false)
        channel1.channelName.text = getString(R.string.create_receipt_channel_1)

        channel2 = layoutInflater.inflate(R.layout.content_create_receipt_data, channelContainer, false)
        channel2.channelName.text = getString(R.string.create_receipt_channel_2)

        listOf(channel1, channel2).forEachIndexed { index, view: View ->
            /*view.createReceiptAmperageTextView.text = getString(R.string.create_receipt_amperage_format, "230")
            view.createReceiptPulseDurabilityTextView.text = getString(R.string.create_receipt_pulse_durability_format, "10")
            view.createReceiptPulseFrequencyTextView.text = getString(R.string.create_receipt_pulse_frequency_format, "10")*/
            view.visibility = View.GONE
            view.apply {
                amperageSeekBar.max = AMPERAGE_MAX - AMPERAGE_MIN
                amperageSeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    amperageTextView.text =
                            getString(R.string.create_receipt_amperage_format, (AMPERAGE_MIN + progress).toString())
                }
                amperageSeekBar.isEnabled = false
                amperageSeekBar.progress = 230 - AMPERAGE_MIN

                view.channelStatusSwitch.isClickable = false
                view.bipolarRegimeSwitch.isClickable = false

                pulseDurabilitySeekBar.max = DURABILITY_MAX - DURABILITY_MIN
                pulseDurabilitySeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    pulseDurabilityTextView.text =
                            getString(R.string.create_receipt_pulse_durability_format, (DURABILITY_MIN + progress).toString())
                }
                pulseDurabilitySeekBar.isEnabled = false
                val pulseDurability = (DURABILITY_MAX - DURABILITY_MIN) / 2
                pulseDurabilitySeekBar.progress = pulseDurability
                pulseFrequencyTextView.text =
                        getString(R.string.create_receipt_pulse_frequency_format, (pulseDurability).toString())

                pulseFrequencySeekBar.max = PULSE_FREQUENCY_MAX - PULSE_FREQUENCY_MIN
                pulseFrequencySeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    pulseFrequencyTextView.text =
                            getString(R.string.create_receipt_pulse_frequency_format, (PULSE_FREQUENCY_MIN + progress).toString())
                }
                pulseFrequencySeekBar.isEnabled = false
                val pulseFreqValue = (PULSE_FREQUENCY_MAX - PULSE_FREQUENCY_MIN) / 2
                pulseFrequencySeekBar.progress = pulseFreqValue
                pulseFrequencyTextView.text =
                        getString(R.string.create_receipt_pulse_frequency_format, (pulseFreqValue).toString())

                pulseFormTextView.setOnClickListener { /*presenter.onPulseFormClick(index)*/ }
                pulseFormValueTextView.setOnClickListener { /*presenter.onPulseFormClick(index)*/ }
            }
        }

        channelContainer.addView(channel1)
        channelContainer.addView(channel2)
    }

    override fun setIsSchedule(isSchedule: Boolean) {
        executionTimeTextView.visibleOrGone(isSchedule)
        executionTimeSeekBar.visibleOrGone(isSchedule)
        scheduleViews.forEach { it.visibleOrGone(isSchedule) }
    }

    override fun showTypesDialog(types: Array<String>, positionSelected: Int) {
        AlertDialog.Builder(activity!!)
                .setSingleChoiceItems(types, positionSelected)
                { dialog, i ->
                    presenter.onProgramTypeChosen(i)
                    typeValue.text = types[i]
                    dialog.dismiss()
                }
                .setOnKeyListener { dialog, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) dialog.dismiss()
                    true
                }
                .show()
    }

    override fun showTimeSet(start: Boolean, time: String) {
        if (start) {
            startValue.text = time
        } else {
            endValue.text = time
        }
    }

    override fun showTimePicker(start: Boolean, time: LocalTime) {
        val timePicker: android.app.TimePickerDialog
        timePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            presenter.onTimeChosen(start, LocalTime.of(hourOfDay, minute, 0))
        }, time.hour, time.minute, true)
        timePicker.setTitle(if (start) getString(R.string.create_receipt_start) else getString(R.string.create_receipt_end))
        timePicker.show()
    }

    override fun setProgramDuration(duration: Int) {
        executionTimeSeekBar.progress = duration - EXECUTION_TIME_MIN
    }

    private fun onSaveClick() {
        presenter.onSaveReceiptClick(
                executionTimeSeekBar.progress.toLong() + EXECUTION_TIME_MIN
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }

    companion object {
        val KEY_PROGRAM_ID = "key_program_id"
        val KEY_PROGRAM_USER_CREATED = "key_program_user_created"

        fun newInstance(params: ViewProgramBundle): ViewProgramFragment? {
            val viewFragment = ViewProgramFragment()
            val bundle = Bundle()
            bundle.putString(ViewProgramFragment.KEY_PROGRAM_ID, params.programId)
            bundle.putBoolean(ViewProgramFragment.KEY_PROGRAM_USER_CREATED, params.programIsUserCreated)
            viewFragment.arguments = bundle
            return viewFragment
        }
    }


}

data class ViewProgramBundle(val programId: String, val programIsUserCreated: Boolean)