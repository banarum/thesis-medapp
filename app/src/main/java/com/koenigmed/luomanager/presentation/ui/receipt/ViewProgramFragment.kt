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
import kotlinx.android.synthetic.main.content_view_receipt_data.view.*
import kotlinx.android.synthetic.main.fragment_view_program.*
import kotlinx.android.synthetic.main.toolbar.*
import org.threeten.bp.LocalTime
import timber.log.Timber
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class ViewProgramFragment : BaseFragment(), ViewReceiptView {
    override val layoutRes: Int
        get() = R.layout.fragment_view_program

    @InjectPresenter
    lateinit var presenter: ViewProgramPresenter

    private val channels: MutableList<View> = mutableListOf()

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
    }

    private fun initToolbar() {
        toolbar.apply {
            setNavigationOnClickListener { presenter.onBackPressed() }
            inflateMenu(R.menu.menu_view_receipt)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_view_receipt_delete -> {
                        deleteProgram()
                    }
                    R.id.action_view_receipt_save -> {
                        onSaveClick()
                    }
                    R.id.action_view_receipt_duplicate -> {
                        presenter.onDuplicate()
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

    override fun setProgramChannels(data: List<ChannelData>) {
        channelContainer.removeAllViews()
        channels.clear()

        data.forEach { item ->
            addChannel().apply {
                visibility = View.VISIBLE
                channelStatus.text = (if (item.isEnabled) "Вкл." else "Выкл")
                bipolarStatus.text = (if (item.bipolar) "Вкл." else "Выкл")
                pulseFormValueTextView.text = item.pulseForm?.name

                amperageTextView.text =
                        getString(R.string.create_receipt_amperage_format, (item.amperage).toString())

                channelName.text = if (item.channelIndex==1) "Сеанс ${channels.size/2 + 1}\n\n"+getString(R.string.create_receipt_channel_1) else getString(R.string.create_receipt_channel_2)
                pulseDurabilityTextView.text =
                        getString(R.string.create_receipt_pulse_durability_format, (item.durationMs).toString())
                pulsePauseTextView.text = getString(R.string.create_receipt_pulse_pause_format, (item.pauseMs).toString())
                pulseFrequencyTextView.text =
                        getString(R.string.create_receipt_pulse_frequency_format, (item.frequency).toString())
            }
        }
    }

    fun deleteProgram() {
        AlertDialog.Builder(activity!!)
                .setTitle("Удаление программы")
                .setMessage("Вы уверенны, что хотите удалить программу?")
                .setPositiveButton("Да", { dialog, i ->
                    presenter.deleteProgram()
                    dialog.dismiss()
                })
                .setNegativeButton("Нет", {dialog, i ->
                    dialog.dismiss()
                })
                .setOnKeyListener { dialog, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) dialog.dismiss()
                    true
                }
                .show()
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

    private fun addChannel(): View {
        val channel = layoutInflater.inflate(R.layout.content_view_receipt_data, channelContainer, false)
        channel.visibility = View.GONE
        return channel.apply {

            channels.add(this)
            channelContainer.addView(this)
        }
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
            bundle.putString(KEY_PROGRAM_ID, params.programId)
            bundle.putBoolean(KEY_PROGRAM_USER_CREATED, params.programIsUserCreated)
            viewFragment.arguments = bundle
            return viewFragment
        }
    }


}

data class ViewProgramBundle(val programId: String, val programIsUserCreated: Boolean)