package com.koenigmed.luomanager.presentation.ui.program

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.inflate
import com.koenigmed.luomanager.extension.visibleOrGone
import com.koenigmed.luomanager.presentation.mvp.program.MyoProgramPresentation
import com.koenigmed.luomanager.presentation.ui.widget.BetterCheckBox
import com.koenigmed.luomanager.util.RxUtil
import kotlinx.android.synthetic.main.item_program.view.*
import timber.log.Timber

class ProgramDelegate(
        private val switchListener: (String) -> Unit,
        private val deleteListener: (String) -> Unit,
        private val clickListener: (MyoProgramPresentation) -> Unit,
        private val longClickListener: (MyoProgramPresentation) -> Boolean
) : AdapterDelegate<MutableList<Any>>() {
    var multiSelect: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ProgramViewHolder(parent.inflate(R.layout.item_program))

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
            items[position] is MyoProgramPresentation

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as ProgramViewHolder).bind(items[position] as MyoProgramPresentation)
    }

    private inner class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var program: MyoProgramPresentation
        //private lateinit var betterSwitch: BetterCheckBox

        private val onCheckedChangeListener: CompoundButton.OnCheckedChangeListener =
                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    buttonView.isClickable = false

                    if (program.isSelected) {
                        //betterSwitch.silentlySetChecked(true)
                    } else {
                        switchListener(program.id)
                        program.isSelected = true
                    }

                    RxUtil.runWithDelay({
                        buttonView?.let {
                            it.isClickable = true
                        }
                    }, 600)
                }

        fun bind(program: MyoProgramPresentation) {
            this.program = program
            with(itemView) {
                programTitle.text = program.name
                itemView.setOnClickListener {
                    clickListener(program)
                }
                itemView.setOnLongClickListener {
                    longClickListener(program)
                }
                if (multiSelect) {
                    //programSwitch.visibleOrGone(false)
                    if (program.createdByUser) {
                        programDelete.visibleOrGone(true)
                        programDelete.setOnClickListener {
                            deleteListener.invoke(program.id)
                        }
                    }
                } else {
                    //programSwitch.visibleOrGone(true)
                    programDelete.visibleOrGone(false)
                    //betterSwitch = BetterCheckBox(itemView.context, programSwitch)
                    //betterSwitch.silentlySetChecked(program.isSelected)
                    //betterSwitch.setOnCheckedChangeListener(onCheckedChangeListener)
                }
            }
        }
    }
}