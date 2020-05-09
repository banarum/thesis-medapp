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
import com.koenigmed.luomanager.presentation.mvp.receipt.ChannelData
import com.koenigmed.luomanager.presentation.mvp.receipt.CreateProgramPresenter
import com.koenigmed.luomanager.presentation.mvp.receipt.CreateReceiptView
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import kotlinx.android.synthetic.main.content_create_receipt_data.view.*
import kotlinx.android.synthetic.main.fragment_create_program.*
import kotlinx.android.synthetic.main.toolbar.*
import org.threeten.bp.LocalTime
import toothpick.Toothpick

class CreateProgramFragment : BaseFragment(), CreateReceiptView {
    override val layoutRes: Int
        get() = R.layout.fragment_create_program

    @InjectPresenter
    lateinit var presenter: CreateProgramPresenter

    private lateinit var channel1: View
    private lateinit var channel2: View

    private lateinit var scheduleViews: List<View>

    @ProvidePresenter
    fun providePresenter(): CreateProgramPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(CreateProgramPresenter::class.java)
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
            title = getString(R.string.create_receipt_title)
            setNavigationOnClickListener { presenter.onBackPressed() }
            inflateMenu(R.menu.menu_create_receipt)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_create_receipt_save -> {
                        onSaveClick()
                    }
                }
                true
            }
        }
    }

    private fun initData() {
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

        start.setOnClickListener { presenter.onStartClick() }
        startValue.setOnClickListener { presenter.onStartClick() }
        end.setOnClickListener { presenter.onEndClick() }
        endValue.setOnClickListener { presenter.onEndClick() }
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
            view.apply {
                amperageSeekBar.max = AMPERAGE_MAX - AMPERAGE_MIN
                amperageSeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    amperageTextView.text =
                            getString(R.string.create_receipt_amperage_format, (AMPERAGE_MIN + progress).toString())
                }
                amperageSeekBar.progress = 230 - AMPERAGE_MIN

                pulseDurabilitySeekBar.max = DURABILITY_MAX - DURABILITY_MIN
                pulseDurabilitySeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    pulseDurabilityTextView.text =
                            getString(R.string.create_receipt_pulse_durability_format, (DURABILITY_MIN + progress).toString())
                }
                val pulseDurability = (DURABILITY_MAX - DURABILITY_MIN) / 2
                pulseDurabilitySeekBar.progress = pulseDurability
                pulseFrequencyTextView.text =
                        getString(R.string.create_receipt_pulse_frequency_format, (pulseDurability).toString())

                pulseFrequencySeekBar.max = PULSE_FREQUENCY_MAX - PULSE_FREQUENCY_MIN
                pulseFrequencySeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                    pulseFrequencyTextView.text =
                            getString(R.string.create_receipt_pulse_frequency_format, (PULSE_FREQUENCY_MIN + progress).toString())
                }
                val pulseFreqValue = (PULSE_FREQUENCY_MAX - PULSE_FREQUENCY_MIN) / 2
                pulseFrequencySeekBar.progress = pulseFreqValue
                pulseFrequencyTextView.text =
                        getString(R.string.create_receipt_pulse_frequency_format, (pulseFreqValue).toString())

                pulseFormTextView.setOnClickListener { presenter.onPulseFormClick(index) }
                pulseFormValueTextView.setOnClickListener { presenter.onPulseFormClick(index) }
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

    override fun showTimePicker(start: Boolean, time: LocalTime) {
        val timePicker: android.app.TimePickerDialog
        timePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            presenter.onTimeChosen(start, LocalTime.of(hourOfDay, minute, 0))
        }, time.hour, time.minute, true)
        timePicker.setTitle(if (start) getString(R.string.create_receipt_start) else getString(R.string.create_receipt_end))
        timePicker.show()
    }

    override fun showTimeSet(start: Boolean, time: String) {
        if (start) {
            startValue.text = time
        } else {
            endValue.text = time
        }
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

    override fun showPulseFormsDialog(forms: List<PulseForm>, checkedItemPosition: Int, channelIndex: Int) {
        val formsNames = forms.map { it.name }
                .toList()
                .toTypedArray()
        AlertDialog.Builder(activity!!)
                .setSingleChoiceItems(
                        formsNames,
                        checkedItemPosition)
                { dialog, i ->
                    presenter.onPulseFormChosen(forms[i], channelIndex)
                    when (channelIndex) {
                        0 -> channel1.pulseFormValueTextView.text = formsNames[i]
                        1 -> channel2.pulseFormValueTextView.text = formsNames[i]
                    }
                    dialog.dismiss()
                }
                .setOnKeyListener { dialog, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                    }
                    true
                }
                .show()
    }

    private fun onSaveClick() {
        val channel1Data = getChannelData(channel1)
        val channel2Data = getChannelData(channel2)
        presenter.onSaveReceiptClick(
                name.text.toString(),
                executionTimeSeekBar.progress.toLong() + EXECUTION_TIME_MIN,
                channel1Data,
                channel2Data
        )
    }

    private fun getChannelData(channel: View): ChannelData {
        return ChannelData(
                channel.channelStatusSwitch.isChecked,
                null,
                channel.bipolarRegimeSwitch.isChecked,
                channel.amperageSeekBar.progress,
                channel.pulseDurabilitySeekBar.progress.toLong() + DURABILITY_MIN,
                channel.pulseFrequencySeekBar.progress + PULSE_FREQUENCY_MIN
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }

    companion object {
        const val EXECUTION_TIME_MIN = 1
        const val EXECUTION_TIME_MAX = 30
        const val AMPERAGE_MIN = 0
        const val AMPERAGE_MAX = 10
        const val DURABILITY_MIN = 10
        const val DURABILITY_MAX = 10000
        const val PULSE_FREQUENCY_MIN = 20
        const val PULSE_FREQUENCY_MAX = 2000
    }
}