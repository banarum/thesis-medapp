package com.koenigmed.luomanager.presentation.ui.treatment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.koenigmed.luomanager.R
import com.koenigmed.luomanager.extension.getTimeString
import com.koenigmed.luomanager.extension.getTimeStringFromSeconds
import com.koenigmed.luomanager.extension.inflate
import com.koenigmed.luomanager.presentation.mvp.treatment.MyoProgramHistoryPresentation
import kotlinx.android.synthetic.main.item_treatment_history_task.view.*

class TreatmentTaskDelegate(context: Context) : AdapterDelegate<MutableList<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            TreatmentTaskViewHolder(parent.inflate(R.layout.item_treatment_history_task))

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is MyoProgramHistoryPresentation

    override fun onBindViewHolder(items: MutableList<Any>, position: Int, holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        (holder as TreatmentTaskDelegate.TreatmentTaskViewHolder).bind(items[position] as MyoProgramHistoryPresentation)
    }

    private inner class TreatmentTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: MyoProgramHistoryPresentation) {
            with(itemView) {
                treatmentHistoryTaskTimeStart.text = item.startTime.toLocalTime().getTimeString()
                treatmentHistoryTaskName.text = item.name
                treatmentHistoryTaskTime.text = item.executionTimeS.getTimeStringFromSeconds(context)
            }
        }
    }

}