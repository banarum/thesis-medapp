package com.koenigmed.luomanager.presentation.ui.profile

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.EditText
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getDimen
import com.koenigmed.luomanager.presentation.mvp.profile_edit.ProfileEditPresenter
import com.koenigmed.luomanager.presentation.mvp.profile_edit.ProfileEditView
import com.koenigmed.luomanager.presentation.mvp.profile_edit.UserDataPresentation
import com.koenigmed.luomanager.presentation.ui.global.BaseFragment
import com.koenigmed.luomanager.toothpick.DI
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.content_profile_edit_fields.*
import kotlinx.android.synthetic.main.toolbar.*
import org.threeten.bp.LocalDate
import toothpick.Toothpick

class ProfileEditFragment : BaseFragment(), ProfileEditView, DatePickerDialog.OnDateSetListener {

    override val layoutRes = R.layout.fragment_profile_edit

    @InjectPresenter
    lateinit var presenter: ProfileEditPresenter

    @ProvidePresenter
    fun providePresenter(): ProfileEditPresenter {
        return Toothpick
                .openScope(DI.MAIN_ACTIVITY_SCOPE)
                .getInstance(ProfileEditPresenter::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.back_title)
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.menu_profile_edit)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_profile_edit_save -> {
                        presenter.onSaveClick()
                    }
                }
                true
            }
        }
    }

    override fun showProfileData(userData: UserDataPresentation) {
        profileEditNameTextView.text = userData.name
        profileEditSurnameTextView.text = userData.surname
        profileEditAgeTextView.text = userData.getAgeString()
        profileEditHeightTextView.text = userData.height
        profileEditWeightTextView.text = userData.weight

        setListeners()
    }

    private fun setListeners() {
        profileEditSurnameTitleTextView.setOnClickListener { presenter.onSurnameClick() }
        profileEditSurnameTextView.setOnClickListener { presenter.onSurnameClick() }

        profileEditNameTitleTextView.setOnClickListener { presenter.onNameClick() }
        profileEditNameTextView.setOnClickListener { presenter.onNameClick() }

        profileEditAgeTitleTextView.setOnClickListener { presenter.onAgeClick() }
        profileEditAgeTextView.setOnClickListener { presenter.onAgeClick() }

        profileEditHeightTitleTextView.setOnClickListener { presenter.onHeightClick() }
        profileEditHeightTextView.setOnClickListener { presenter.onHeightClick() }

        profileEditWeightTitleTextView.setOnClickListener { presenter.onWeightClick() }
        profileEditWeightTextView.setOnClickListener { presenter.onWeightClick() }
    }

    override fun showNameDialog(name: String) {
        val alert = AlertDialog.Builder(activity!!)
        val editText = EditText(activity)
        editText.apply {
            inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setText(name)
            setSelection(length())
        }
        alert.setTitle(getString(R.string.auth_register_name_hint))
        alert.setView(editText)

        alert.setPositiveButton(getString(R.string.profile_edit_dialog_ok)) { _, _ ->
            presenter.onNameEntered(editText.text.toString())
        }

        alert.show()

        val margin = context!!.getDimen(R.dimen.profile_edit_dialog_edit_text_margin)
        editText.apply {
            (layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)
        }
    }

    override fun showSurnameDialog(surname: String) {
        val alert = AlertDialog.Builder(activity!!)
        val editText = EditText(activity)
        editText.apply {
            inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setText(surname)
            setSelection(length())
        }
        alert.setTitle(getString(R.string.auth_register_surname_hint))
        alert.setView(editText)

        alert.setPositiveButton(getString(R.string.profile_edit_dialog_ok)) { _, _ ->
            presenter.onSurnameEntered(editText.text.toString())
        }

        alert.show()

        val margin = context!!.getDimen(R.dimen.profile_edit_dialog_edit_text_margin)
        editText.apply {
            (layoutParams as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)
        }
    }

    override fun showAgeDatePicker(date: LocalDate) {
        val dpd = DatePickerDialog.newInstance(
                this,
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
        )
        dpd.show(activity!!.fragmentManager, "AgeDatePickerDialog")
    }

    override fun showHeightPicker(height: Int) {
        var position = -1
        var i = 0
        val items: Array<out String> = IntArray(200) { i -> i + 40 }
                .map {
                    if (height == it) position = i else i++
                    getString(R.string.height_measure_format, it.toString())
                }
                .toList()
                .toTypedArray()
        AlertDialog.Builder(activity!!)
                .setSingleChoiceItems(items, position) { dialog, i ->
                    presenter.onHeightSet(items[i])
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

    override fun showWeightPicker(weight: Int) {
        var position = -1
        var i = 0
        val weights: Array<out String> = IntArray(120) { i -> i + 20 }
                .map {
                    if (weight == it) position = i else i++
                    getString(R.string.weight_measure_format, it.toString())
                }
                .toList()
                .toTypedArray()
        AlertDialog.Builder(activity!!)
                .setSingleChoiceItems(weights, position) { dialog, i ->
                    presenter.onWeightSet(weights[i])
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

    override fun showSurname(surname: String) {
        profileEditSurnameTextView.text = surname
    }

    override fun showName(name: String) {
        profileEditNameTextView.text = name
    }

    override fun showAge(age: String) {
        profileEditAgeTextView.text = age
    }

    override fun showHeight(height: String) {
        profileEditHeightTextView.text = height
    }

    override fun showWeight(weight: String) {
        profileEditWeightTextView.text = weight
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        presenter.onAgeSet(LocalDate.of(year, monthOfYear + 1, dayOfMonth))
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

}