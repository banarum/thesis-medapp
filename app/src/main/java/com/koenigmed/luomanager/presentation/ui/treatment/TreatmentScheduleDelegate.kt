package com.koenigmed.luomanager.presentation.ui.treatment

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getTimeString
import com.koenigmed.luomanager.extension.inflate
import kotlinx.android.synthetic.main.item_treatment_schedule.view.*
import org.threeten.bp.LocalTime

class TreatmentScheduleDelegate(
) : AdapterDelegate<MutableList<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            TimeViewHolder(parent.inflate(R.layout.item_treatment_schedule))

    override fun isForViewType(items: MutableList<Any>, position: Int): Boolean =
            items[position] is LocalTime

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as TimeViewHolder).bind(items[position] as LocalTime)
    }

    private inner class TimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(time: LocalTime) {
            with(itemView) {
                itemScheduleTextView.text = time.getTimeString()
                val alpha = if (LocalTime.now().isAfter(time)) 0.2f else 1.0f
                itemScheduleTextView.alpha = alpha
            }
        }
    }
}