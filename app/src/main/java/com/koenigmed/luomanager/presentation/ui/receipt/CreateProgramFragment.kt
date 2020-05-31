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
import timber.log.Timber
import toothpick.Toothpick

class CreateProgramFragment : BaseFragment(), CreateReceiptView {
    override val layoutRes: Int
        get() = R.layout.fragment_create_program

    @InjectPresenter
    lateinit var presenter: CreateProgramPresenter

    private var channels: MutableList<View> = mutableListOf()

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

        if (this.arguments != null) {
            this.presenter.setProgramPresentation(this.arguments!!.getString(KEY_PROGRAM_ID, ""), this.arguments!!.getBoolean(KEY_EDIT, true))
        }
        this.toolbar.title = if (this.arguments!!.getBoolean(KEY_EDIT, false)) "Редактирование" else getString(R.string.create_receipt_title)
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
        add_channel.setOnClickListener { addChannels() }
    }

    private fun addChannel(id: Int): View {
        val channel = layoutInflater.inflate(R.layout.content_create_receipt_data, channelContainer, false)
        channel.visibility = View.GONE
        return channel.apply {
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

            pulsePauseSeekBar.max = DURABILITY_MAX - DURABILITY_MIN
            pulsePauseSeekBar.onProgressChanged { _: SeekBar?, progress: Int, _: Boolean ->
                pulsePauseTextView.text =
                        getString(R.string.create_receipt_pulse_pause_format, (DURABILITY_MIN + progress).toString())
            }

            pulsePauseSeekBar.progress = pulseDurability

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

            val currentIndex = channels.size

            if (id%2==1) {
                delete_program.setOnClickListener {
                    this.visibility = View.GONE
                    channels[channels.indexOf(this) + 1].visibility = View.GONE
                    channels.removeAt(channels.indexOf(this) + 1)
                    channels.removeAt(channels.indexOf(this))

                }
            } else {
                delete_program.visibility = View.GONE
            }



            pulseFormTextView.setOnClickListener { presenter.onPulseFormClick(currentIndex) }
            pulseFormValueTextView.setOnClickListener { presenter.onPulseFormClick(currentIndex) }
            presenter.setChannelIndex(currentIndex, id)

            channels.add(this)
            channelContainer.addView(this)
        }
    }

    private fun addChannels() {
        val channel1 = this.addChannel(1).apply {
            visibility = View.VISIBLE
            channelName.text = "Сеанс ${channels.size/2 + 1}\n\n"+getString(R.string.create_receipt_channel_1)
        }

        val channel2 = this.addChannel(2).apply {
            visibility = View.VISIBLE
            channelName.text = getString(R.string.create_receipt_channel_2)
        }

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
                    channels[channelIndex].pulseFormValueTextView.text = formsNames[i]
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
        presenter.onSaveReceiptClick(
                name.text.toString(),
                executionTimeSeekBar.progress.toLong() + EXECUTION_TIME_MIN,
                channels.map { getChannelData(it) }
        )
    }

    private fun getChannelData(channel: View): ChannelData {
        return ChannelData(
                channel.channelStatusSwitch.isChecked,
                null,
                channel.bipolarRegimeSwitch.isChecked,
                channel.amperageSeekBar.progress,
                channel.pulseDurabilitySeekBar.progress.toLong() + DURABILITY_MIN,
                channel.pulsePauseSeekBar.progress.toLong() + DURABILITY_MIN,
                channel.pulseFrequencySeekBar.progress + PULSE_FREQUENCY_MIN,
                1
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackPressed()
    }

    override fun setProgramTitle(title: String, editable: Boolean) {
        name.setText(title)
        name.isEnabled = editable
    }

    override fun setProgramChannels(data: List<ChannelData>) {
        data.forEach { item ->
            addChannel(item.channelIndex).apply {
                visibility = View.VISIBLE
                channelStatusSwitch.isChecked = item.isEnabled
                pulseFormValueTextView.text = item.pulseForm?.name
                bipolarRegimeSwitch.isChecked = item.bipolar
                amperageSeekBar.progress = item.amperage
                pulseDurabilitySeekBar.progress = item.durationMs.toInt() - DURABILITY_MIN
                pulseFrequencySeekBar.progress = item.frequency - PULSE_FREQUENCY_MIN
                channelName.text = if (item.channelIndex==1) "Сеанс ${channels.size/2 + 1}\n\n"+getString(R.string.create_receipt_channel_1) else getString(R.string.create_receipt_channel_2)
            }
        }
    }

    override fun setProgramDuration(duration: Int) {
        executionTimeSeekBar.progress = duration - EXECUTION_TIME_MIN
    }

    override fun setProgramStartTime(time: String) {
        startValue.text = time
    }

    override fun setProgramEndTime(time: String) {
        endValue.text = time
    }

    override fun setProgramType(typeName: String, typeId: Int) {
        typeValue.text = typeName
        presenter.onProgramTypeChosen(typeId)
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

        val KEY_PROGRAM_ID = "key_program_id"
        val KEY_EDIT = "key_EDIT"

        fun newInstance(programBundle: InputBundle): CreateProgramFragment? {
            val createFragment = CreateProgramFragment()
            val bundle = Bundle()
            bundle.putString(KEY_PROGRAM_ID, programBundle.programId)
            bundle.putBoolean(KEY_EDIT, programBundle.edit)
            createFragment.arguments = bundle
            return createFragment
        }
    }
    data class InputBundle(val programId: String?, val edit: Boolean=false)
}