package com.koenigmed.luomanager.presentation.ui.widget

import android.content.Context
import android.widget.CheckBox
import android.widget.CompoundButton

class BetterCheckBox(context: Context) : CheckBox(context) {
    private var myListener: CompoundButton.OnCheckedChangeListener? = null
    private lateinit var myCheckBox: CompoundButton

    constructor(context: Context, checkBox: CompoundButton) : this(context) {
        this.myCheckBox = checkBox
    }

    // assorted constructors here...

    override fun setOnCheckedChangeListener(
            listener: CompoundButton.OnCheckedChangeListener?) {
        if (listener != null) {
            this.myListener = listener
        }
        myCheckBox.setOnCheckedChangeListener(listener)
    }

    fun silentlySetChecked(checked: Boolean) {
        toggleListener(false)
        myCheckBox.isChecked = checked
        toggleListener(true)
    }

    private fun toggleListener(on: Boolean) {
        if (on) {
            this.setOnCheckedChangeListener(myListener)
        } else {
            this.setOnCheckedChangeListener(null)
        }
    }
}